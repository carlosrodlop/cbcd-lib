/**
 * Copy and Paste this DSL code into DevOps Essencial DSL Editor in CloudBees CD Server
 * Tested in 2023.03.1.161626
 * Outputs:
 * - Project to host Admin Resources, including:
 *  - Procedure to create Upstream Release Pipeline
 *  - Downstream Release Pipeline
 *  - Application Deployment Analytics Dashboard
 *  - Environment to deploy Applications
 *  - Service Catalog (Template) ==> Catalog Item ==> Onboard New Application. Outputs:
 *    - Project per Team - Product Base Line
 *    - First Upstream Release Pipeline
 *    - Service Catalog (Template) ==> Catalog Item ==> New Release for existing Application. Outputs:
 *      - Upstream Release Pipeline consecutive Releases
 */

// General
def org='ExampleOrg'
def version='1.0.0'
// Admin Project
def varAdminProjectName = org + '-ADMIN'
def varAdminGroupCDServer = 'SDAAdmins'
def varDownReleaseName = org + '_DOWNSTREAM_RELEASE_' + version
def varDownPipelineName = 'pipeline_' + varDownReleaseName
def varProcedureCreateUp = org + '_CREATE_UPSTREAM_' + version
def varAnalyticsDashboard = org + '_APPLICATIONS_DEPLOYMENT_' + version
def varEnvironmentName = org + '_ENV_A'
// Applications Project
def varCatalogNameNewApp = org + '_NEW_APPLICATION_' + version
def varCatalogNameNewAppItem = varCatalogNameNewApp
def varCatalogNameNewRelease = org + '_NEW_RELEASE_' + version
def varCatalogNameNewReleaseItem = varCatalogNameNewRelease

// Project Admin
project varAdminProjectName, {
  // Project Admin - Procedure: Create Upstream Pipeline
  procedure varProcedureCreateUp, {
    projectName = varAdminProjectName

    formalParameter 'projName', defaultValue: '', {
      expansionDeferred = '0'
      label = 'Project Name'
      orderIndex = '1'
      required = '1'
      type = 'entry'
    }

    formalParameter 'releaseName', defaultValue: '', {
      expansionDeferred = '0'
      label = 'Release Name'
      orderIndex = '2'
      required = '1'
      type = 'entry'
    }

    formalParameter 'version', defaultValue: '', {
      expansionDeferred = '0'
      label = 'Version'
      orderIndex = '3'
      required = '1'
      type = 'entry'
    }

    formalParameter 'artifacts', defaultValue: '', {
      expansionDeferred = '0'
      label = 'Version'
      orderIndex = '4'
      required = '1'
      type = 'entry'
    }

    formalParameter 'param1', defaultValue: '', {
      expansionDeferred = '0'
      label = 'Param 1'
      orderIndex = '5'
      required = '1'
      type = 'entry'
    }

    formalParameter 'param2', defaultValue: '', {
      expansionDeferred = '0'
      label = 'Param 2'
      orderIndex = '6'
      required = '1'
      type = 'entry'
    }

    formalParameter 'projectProp', defaultValue: '', {
      expansionDeferred = '0'
      label = 'Project Prop'
      orderIndex = '7'
      required = '1'
      type = 'entry'
    }

    formalParameter 'teamGroup', defaultValue: '', {
      expansionDeferred = '0'
      label = 'Team Group'
      orderIndex = '6'
      required = '1'
      type = 'entry'
    }

    step 'Create Upstream', {
      description = ''
      alwaysRun = '0'
      broadcast = '0'
      command = '''varTeamGroup = "$[teamGroup]"
varProjectName = "$[projName]"
varReleaseName = "$[releaseName]"
varVersion = "$[version]"
varArtifacts = "$[artifacts]"
varPipelineName = \'pipeline_\' + varReleaseName
varParam1 = "$[param1]"
varParam2 = "$[param2]"
varProjectProp = "$[projectProp]"

release varReleaseName, {
  projectName = varProjectName
  pipeline varPipelineName, { // Upstream Pipeline Definition
    releaseName = varReleaseName
    formalParameter \'param1\', defaultValue: varParam1, {
      expansionDeferred = \'0\'
      label = \'Param 1\'
      orderIndex = \'1\'
      required = \'1\'
      type = \'entry\'
    }
    formalParameter \'param2\', defaultValue: varParam2, {
      expansionDeferred = \'0\'
      label = \'Param 2\'
      orderIndex = \'2\'
      required = \'1\'
      type = \'entry\'
    }
    formalParameter \'projParam\', defaultValue: varProjectProp, {
      expansionDeferred = \'0\'
      label = \'Project Param\'
      orderIndex = \'3\'
      required = \'1\'
      type = \'entry\'
    }
    stage \'Stage 1\', {
      pipelineName = varPipelineName
      task \'Call downstream\', {
        actualParameter = [
        \'param1\': varParam1,
        \'param2\': varParam2,
        \'projParam\': varProjectProp,
        ]
        errorHandling = \'stopOnError\'
        subErrorHandling = \'continueOnError\'
        subproject = \'''' + varAdminProjectName + '''\'
        subrelease = \'''' + varDownReleaseName + '''\'
        taskType = \'RELEASE\'
        triggerType = \'async\'
        } // End Task Call downstream
      task \'Analytics\', {
        advancedMode = \'0\'
        allowOutOfOrderRun = \'0\'
        alwaysRun = \'1\'
        applicationName = varProjectName
        applicationProjectName = varProjectName
        applicationVersion = varVersion
        artifacts = varArtifacts
        command = \'echo "Pushing to analytics"\'
        enabled = \'1\'
        environmentName = \'''' + varEnvironmentName + '''\'
        environmentProjectName = \'''' + varAdminProjectName + '''\'
        errorHandling = \'stopOnError\'
        insertRollingDeployManualStep = \'0\'
        skippable = \'0\'
        subTaskType = \'DEPLOY\'
        taskType = \'COMMAND\'
        useApproverAcl = \'0\'
        } // End Task Analystics
      }//End Stage
  }//End Pipeline
  subrelease {
    subreleaseName = \'''' + varDownReleaseName + '''\'
    subreleaseProject = \'''' + varAdminProjectName + '''\'
  }
  acl {
    inheriting = \'1\'
    // Update this part for release or promotion respectively
    aclEntry \'user\', principalName: varTeamGroup, {
      changePermissionsPrivilege = \'inherit\'
      executePrivilege = \'allow\'
      modifyPrivilege = \'inherit\'
      readPrivilege = \'inherit\'
    }
  }//End ACL Release
}//End Release'''
      errorHandling = 'failProcedure'
      exclusiveMode = 'none'
      parallel = '0'
      releaseMode = 'none'
      shell = 'ectool evalDsl --dslFile \'{0}\''
      subprocedure = null
      subproject = null
      timeLimit = '0'
      timeLimitUnits = 'seconds'
    }
    acl {
      inheriting = '1'

      aclEntry 'group', principalName: varAdminGroupCDServer, {
        changePermissionsPrivilege = 'allow'
        executePrivilege = 'allow'
        modifyPrivilege = 'allow'
        readPrivilege = 'allow'
      }

      aclEntry 'group', principalName: 'Everyone', {
        changePermissionsPrivilege = 'inherit'
        executePrivilege = 'inherit'
        modifyPrivilege = 'inherit'
        readPrivilege = 'allow'
      }
    }
  } // End of Procedure Create Upstream Pipeline
  // Project Admin - Release Pipeline: Downstream Release
  release varDownReleaseName, {
    projectName = varAdminProjectName
    pipeline varDownPipelineName, {
      releaseName = varDownReleaseName
      formalParameter 'param1', defaultValue: '', {
        expansionDeferred = '0'
        label = 'Param 1'
        orderIndex = '1'
        required = '1'
        type = 'entry'
      }
      formalParameter 'param2', defaultValue: '', {
        expansionDeferred = '0'
        label = 'Param 2'
        orderIndex = '2'
        required = '1'
        type = 'entry'
      }
      formalParameter 'projParam', defaultValue: '', {
        expansionDeferred = '0'
        label = 'Project Param'
        orderIndex = '3'
        required = '1'
        type = 'entry'
      }
      stage 'Stage 1', {
        pipelineName = varDownPipelineName
        task 'echo', {
          command = '''echo "Release Param 1:  $[param1]"
echo "Release Param 2: $[param2]"
echo "Project Param: $[projParam]"
'''
          enabled = '1'
          errorHandling = 'stopOnError'
          resourceName = ''
          taskType = 'COMMAND'
        }
        task 'Project Properties', {
          command = '''ectool setProperty \'/projects/$[/myPipelineRuntime/projectName]/Myprop\' \'value\'
'''
          enabled = '1'
          errorHandling = 'stopOnError'
          taskType = 'COMMAND'
        }
      }
      stage 'Stage 2', {
        colorCode = '#ff7f0e'
        completionType = 'auto'
        pipelineName = varDownPipelineName
      }
      stage 'Stage 3', {
        colorCode = '#2ca02c'
        completionType = 'auto'
        pipelineName = varDownPipelineName
      }
      // Custom properties
      property 'ec_counters', {
        // Custom properties
        pipelineCounter = '7'
      }
    }
    acl {
      inheriting = '1'

      aclEntry 'group', principalName: varAdminGroupCDServer, {
        changePermissionsPrivilege = 'allow'
        executePrivilege = 'allow'
        modifyPrivilege = 'allow'
        readPrivilege = 'allow'
      }

      aclEntry 'group', principalName: 'Everyone', {
        changePermissionsPrivilege = 'inherit'
        executePrivilege = 'inherit'
        modifyPrivilege = 'inherit'
        readPrivilege = 'allow'
      }
    }
  } // End of Downtream Pipeline
  // Project Admin - Dashboard: Application Deployments
  dashboard 'Application_Deployments', {
    description = 'Application Deployments Dashboard'
    layout = 'FLOW'
    projectName = varAdminProjectName
    type = 'STANDARD'

    reportingFilter 'DateFilter', {
      description = 'Filter deployments by job finish.'
      dashboardName = 'Application_Deployments'
      filters = null
      operator = 'BETWEEN'
      orderIndex = '1'
      parameterName = 'jobFinish'
      reportObjectTypeName = null
      required = '1'
      type = 'DATE'
      widgetName = null
    }

    reportingFilter 'ProjectFilter', {
      description = 'Filter deployments by project name.'
      dashboardName = 'Application_Deployments'
      filters = null
      operator = 'IN'
      orderIndex = '2'
      parameterName = null
      reportObjectTypeName = null
      required = '0'
      type = 'PROJECT'
      widgetName = null
    }

    reportingFilter 'ApplicationFilter', {
      description = 'Filter deployments by application id.'
      dashboardName = 'Application_Deployments'
      filters = null
      operator = 'IN'
      orderIndex = '3'
      parameterName = null
      reportObjectTypeName = null
      required = '0'
      type = 'APPLICATION'
      widgetName = null
    }

    reportingFilter 'EnvironmentFilter', {
      description = 'Filter deployments by environment id.'
      dashboardName = 'Application_Deployments'
      filters = null
      operator = 'IN'
      orderIndex = '4'
      parameterName = null
      reportObjectTypeName = null
      required = '0'
      type = 'ENVIRONMENT'
      widgetName = null
    }

    reportingFilter 'TagFilter', {
      description = 'Filter deployments by tag.'
      dashboardName = 'Application_Deployments'
      filters = null
      operator = 'IN'
      orderIndex = '5'
      parameterName = 'tags'
      reportObjectTypeName = null
      required = '0'
      type = 'TAG'
      widgetName = null
    }

    widget 'TotalDeployments', {
      description = 'Breakdown of total deployments by outcome'
      attributeDataType = [
        'total': 'NUMBER',
        'yAxis': 'NUMBER',
        'xAxis': 'STRING',
      ]
      attributePath = [
        'total': 'total',
        'yAxis': 'deployment_count',
        'xAxis': 'deployment',
      ]
      color = [
        'rollback': '#567b99',
        'aborted': '#808080',
        'success': '#70b723',
        'warning': '#DA833E',
        'error': '#eb1c24',
      ]
      column = null
      dashboardName = 'Application_Deployments'
      iconUrl = null
      linkParameter = [
        'deploymentOutcome': '${deployment}',
      ]
      linkTarget = 'Deployments'
      orderIndex = '29'
      reportName = 'TotalDeployments copy'
      reportProjectName = 'Electric Cloud'
      section = null
      title = null
      visualization = 'DONUT_CHART'
    }

    widget 'SuccessfulDeployments', {
      description = 'Successful deployments metrics'
      attributePath = [
        'duration': 'duration',
        'total': 'total',
      ]
      column = null
      dashboardName = 'Application_Deployments'
      iconUrl = null
      linkTarget = null
      orderIndex = '30'
      reportName = 'SuccessfulDeployments copy'
      reportProjectName = 'Electric Cloud'
      section = null
      title = null
      visualization = 'CUSTOM_CHART'
    }

    widget 'DeploymentsFrequency', {
      description = 'Breakdown of deployments by outcome over time'
      attributeDataType = [
        'yAxis': 'NUMBER',
        'xAxis': 'DATE',
        'groups': 'STRING',
      ]
      attributePath = [
        'yAxis': 'deployment_outcome_count',
        'xAxis': 'deployment_date_label',
        'groups': 'deployment_outcome',
      ]
      color = [
        'rollback': '#567b99',
        'aborted': '#808080',
        'success': '#70b723',
        'warning': '#DA833E',
        'error': '#eb1c24',
      ]
      column = null
      dashboardName = 'Application_Deployments'
      iconUrl = null
      linkParameter = [
        'deploymentDateMax': '${deployment_date_max_label}',
        'deploymentDateMin': '${deployment_date_min_label}',
      ]
      linkTarget = 'Deployments'
      orderIndex = '31'
      reportName = 'DeploymentsFrequency copy'
      reportProjectName = 'Electric Cloud'
      section = null
      title = null
      visualization = 'STACKED_AREA_CHART'
    }

    widget 'AverageDeploymentDuration', {
      description = 'Average deployments duration for successful deployments over time'
      attributeDataType = [
        'yAxis': 'DURATION',
        'xAxis': 'DATE',
      ]
      attributePath = [
        'yAxis': 'avg_duration',
        'xAxis': 'deployment_date_label',
      ]
      column = null
      dashboardName = 'Application_Deployments'
      iconUrl = null
      linkParameter = [
        'deploymentDateMax': '${deployment_date_max_label}',
        'deploymentDateMin': '${deployment_date_min_label}',
      ]
      linkTarget = 'Deployments'
      orderIndex = '32'
      reportName = 'AverageDeploymentDuration copy'
      reportProjectName = 'Electric Cloud'
      section = null
      title = null
      visualization = 'LINE_CHART'
      visualizationProperty = [
        'defaultColor': '#00ADEE',
      ]
    }

    widget 'TopAppsByFailedDeployments', {
      description = 'The top 5 applications with the most number of failed deployments'
      attributeDataType = [
        'column1': 'STRING',
        'column2': 'NUMBER',
      ]
      attributePath = [
        'column1': 'applicationName',
        'column2': 'application_count',
      ]
      column = null
      dashboardName = 'Application_Deployments'
      iconUrl = 'icon-application.svg'
      linkParameter = [
        'deploymentOutcome': 'error',
        'applicationProjectName': '${applicationProjectName}',
        'applicationName': '${applicationName}',
      ]
      linkTarget = 'Deployments'
      orderIndex = '33'
      reportName = 'TopAppsByFailedDeployments copy'
      reportProjectName = 'Electric Cloud'
      section = null
      title = null
      visualization = 'TABLE'
    }

    widget 'TopAppsByDeployments', {
      description = 'The top 5 applications with the most deployments'
      attributeDataType = [
        'yAxis': 'STRING',
        'xAxis': 'NUMBER',
        'stacks': 'STRING',
      ]
      attributePath = [
        'yAxisLabel': 'applicationName',
        'yAxis': 'application',
        'xAxis': 'deployment_outcome_count',
        'stacks': 'deployment_outcome',
      ]
      color = [
        'rollback': '#567b99',
        'aborted': '#808080',
        'success': '#70b723',
        'warning': '#DA833E',
        'error': '#eb1c24',
      ]
      column = null
      dashboardName = 'Application_Deployments'
      iconUrl = 'icon-application.svg'
      linkParameter = [
        'deploymentOutcome': '${deployment_outcome}',
        'applicationProjectName': '${applicationProjectName}',
        'applicationName': '${applicationName}',
      ]
      linkTarget = 'Deployments'
      orderIndex = '34'
      reportName = 'TopAppsByDeployments copy'
      reportProjectName = 'Electric Cloud'
      section = null
      title = null
      visualization = 'STACKED_HORIZONTAL_BAR_CHART'
    }

    widget 'TopEnvsByDeployments', {
      description = 'The top 5 environments with the most deployments'
      attributeDataType = [
        'yAxis': 'STRING',
        'xAxis': 'NUMBER',
        'stacks': 'STRING',
      ]
      attributePath = [
        'yAxisLabel': 'environmentName',
        'yAxis': 'environment',
        'xAxis': 'deployment_outcome_count',
        'stacks': 'deployment_outcome',
      ]
      color = [
        'rollback': '#567b99',
        'aborted': '#808080',
        'success': '#70b723',
        'warning': '#DA833E',
        'error': '#eb1c24',
      ]
      column = null
      dashboardName = 'Application_Deployments'
      iconUrl = 'icon-environments-static.svg'
      linkParameter = [
        'deploymentOutcome': '${deployment_outcome}',
        'environmentName': '${environmentName}',
        'environmentProjectName': '${environmentProjectName}',
      ]
      linkTarget = 'Deployments'
      orderIndex = '35'
      reportName = 'TopEnvsByDeployments copy'
      reportProjectName = 'Electric Cloud'
      section = null
      title = null
      visualization = 'STACKED_HORIZONTAL_BAR_CHART'
    }

    acl {
      inheriting = '1'

      aclEntry 'group', principalName: varAdminGroupCDServer, {
        changePermissionsPrivilege = 'allow'
        executePrivilege = 'allow'
        modifyPrivilege = 'allow'
        readPrivilege = 'allow'
      }

      aclEntry 'group', principalName: 'Everyone', {
        changePermissionsPrivilege = 'inherit'
        executePrivilege = 'inherit'
        modifyPrivilege = 'inherit'
        readPrivilege = 'allow'
      }
    }
  }
  environment varEnvironmentName, {
    environmentEnabled = '1'
    projectName = varAdminProjectName
    reservationRequired = '0'
    rollingDeployEnabled = null
    rollingDeployType = null
    acl {
      inheriting = '1'

      aclEntry 'group', principalName: varAdminGroupCDServer, {
        changePermissionsPrivilege = 'allow'
        executePrivilege = 'allow'
        modifyPrivilege = 'allow'
        readPrivilege = 'allow'
      }

      aclEntry 'group', principalName: 'Everyone', {
        changePermissionsPrivilege = 'inherit'
        executePrivilege = 'inherit'
        modifyPrivilege = 'inherit'
        readPrivilege = 'allow'
      }
    }
  }
  // Project Admin - Service Catalog: Onboard New Applications
  catalog varCatalogNameNewApp, {
    projectName = varAdminProjectName
    catalogItem varCatalogNameNewAppItem, {
      description = '''<xml>
<title>
Onboard New Application for CISCO CD Server. Version: 1.0.0.
</title>
<htmlData>
<![CDATA[
]]>
</htmlData>
</xml>'''
      allowScheduling = '0'
      buttonLabel = 'Create'
      catalogName = varCatalogNameNewApp
      dslString = '''//Constants (mapping)
conPreffix = \'''' + org + '''\'
conAdminProject = \'''' + varAdminProjectName + '''\'
conDownstreamRelease = \'''' + varDownReleaseName + '''\'
// Variables
def varTeam = args.team
def varTeamDeveloperGroup = args.devGroup
def varTeamReleasePMGroup = args.releasePMGroup
def varProjectName = conPreffix + \'-\' + varTeam + \'-\' + args.product
def varVersion = args.version
def varArtifacts = args.artifacts
def varReleaseName = varProjectName + \'-\' + varVersion
def varPipelineName = \'pipeline_\' + varReleaseName
def varParam1 = args.param1
def varParam2 = args.param2
def varProjectProp = args.projectProp

// Applications Project
project varProjectName, {
  // Setter properties to new Project: Team - Application
  ProjectProp1 = varProjectProp
  ReleasePMGroup = varTeamReleasePMGroup
  acl {
    inheriting = \'1\'
    aclEntry \'user\', principalName: varTeam, {
      changePermissionsPrivilege = \'inherit\'
      executePrivilege = \'inherit\'
      modifyPrivilege = \'inherit\'
      readPrivilege = \'allow\'
    }
    aclEntry \'user\', principalName: \'project: \' + varProjectName, {
      changePermissionsPrivilege = \'allow\'
      executePrivilege = \'allow\'
      modifyPrivilege = \'allow\'
      readPrivilege = \'allow\'
    }
  } //End ACL Project
  runProcedure(
    projectName: \"'''+ varAdminProjectName +'''\",
    procedureName: \"'''+ varProcedureCreateUp +'''\",
      actualParameter: [
        releaseName: varReleaseName,
        projName: varProjectName,
        version: varVersion,
        artifacts: varArtifacts,
        param1: varParam1,
        param2: varParam2,
        projectProp: varProjectProp,
        teamGroup: varTeamReleasePMGroup
      ]
    )
    // Applications Project - Service Catalog: New Release
  catalog \'''' + varCatalogNameNewRelease + '''\', {
    projectName = varProjectName
    catalogItem \'''' + varCatalogNameNewReleaseItem + '''\', {
      description = \'\'\'<xml>
<title>
Create New Release from an already onboarded Application for CISCO CD Server. Version: 1.0.0.
</title>
<htmlData>
<![CDATA[
]]>
</htmlData>
</xml>\'\'\'
      allowScheduling = \'0\'
      buttonLabel = \'Create\'
      catalogName = \'''' + varCatalogNameNewRelease + '''\'
      dslString = \'\'\'
// Constants (mapping)
conPreffix = \'''' + org + '''\'
conAdminProject = \'''' + varAdminProjectName + '''\'
conDownstreamRelease = \'''' + varDownReleaseName + '''\'
conProjectName = \\\'\'\'\' + varProjectName + \'\'\'\\\'
// Variables
def varVersion = args.version
def varArtifacts = args.artifacts
def varReleaseName = conProjectName + \\\'-\\\' + varVersion
def varPipelineName = \\\'pipeline_\\\' + varReleaseName
def varParam1 = args.param1
def varParam2 = args.param2
// Getter properties from an onboarded Project
def varProjectProp = getProperties(projectName: conProjectName).ProjectProp1.value
def varTeamReleasePMGroup = getProperties(projectName: conProjectName).ReleasePMGroup.value
runProcedure(
  projectName: \"'''+ varAdminProjectName +'''\",
  procedureName: \"'''+ varProcedureCreateUp +'''\",
  actualParameter: [
    releaseName: varReleaseName,
    projName: conProjectName,
    version: varVersion,
    artifacts: varArtifacts,
    param1: varParam1,
    param2: varParam2,
    projectProp: varProjectProp,
    teamGroup: varTeamReleasePMGroup
        ]
    )\'\'\'
      endTargetJson = \'\'
      iconUrl = \'icon-process.svg\'
      templateObjectType = \'none\'
      useFormalParameter = \'1\'
      formalParameter \'version\', defaultValue: \'1.1.1\', {
        expansionDeferred = \'0\'
        label = \'New Release. Version ID\'
        orderIndex = \'1\'
        required = \'1\'
        type = \'entry\'
        }
      formalParameter 'artifacts', defaultValue: \'com.foo:10.3.0, com.bar:7.0.8\', {
        expansionDeferred = '0'
        label = \'New Release. Artifact list\'
        orderIndex = \'2\'
        required = \'1\'
        type = \'entry\'
      }
      formalParameter \'param1\', defaultValue: \'foo\', {
        expansionDeferred = \'0\'
        label = \'New Release. Property 1\'
        orderIndex = \'3\'
        required = \'1\'
        type = \'entry\'
        }
      formalParameter \'param2\', defaultValue: \'bar\', {
        expansionDeferred = \'0\'
        label = \'New Release. Property 2\'
        orderIndex = \'4\'
        required = \'1\'
        type = \'entry\'
        }
      } // End of catalogItem varCatalogNameNewReleaseItem
      acl {
        inheriting = \'1\'
        aclEntry \'group\', principalName: '''+ varAdminGroupCDServer +''', {
          changePermissionsPrivilege = \'allow\'
          executePrivilege = \'allow\'
          modifyPrivilege = \'allow\'
          readPrivilege = \'allow\'
        }
        aclEntry \'group\', principalName: varTeamReleasePMGroup, {
          changePermissionsPrivilege = \'inherit\'
          executePrivilege = \'allow\'
          modifyPrivilege = \'inherit\'
          readPrivilege = \'allow\'
        }
      } // End of ACL
      } // End of catalog varCatalogNameNewRelease
    } //End Project'''
      endTargetJson = ''
      iconUrl = 'icon-process.svg'
      templateObjectType = 'none'
      useFormalParameter = '1'
      formalParameter 'team', defaultValue: 'SDAAdmins', {
        expansionDeferred = '0'
        label = 'ACL. Team Name'
        orderIndex = '1'
        required = '1'
        type = 'entry'
      }
      formalParameter 'devGroup', defaultValue: 'carlos', {
        expansionDeferred = '0'
        label = 'ACL. Developer Group Name'
        orderIndex = '2'
        required = '1'
        type = 'entry'
      }
      formalParameter 'releasePMGroup', defaultValue: 'carlos', {
        expansionDeferred = '0'
        label = 'ACL. Release PM Group'
        orderIndex = '3'
        required = '1'
        type = 'entry'
      }
      formalParameter 'product', defaultValue: 'ProductExample', {
        expansionDeferred = '0'
        label = 'Product Line'
        orderIndex = '4'
        required = '1'
        type = 'entry'
      }
      formalParameter 'projectProp', defaultValue: 'ACME', {
        expansionDeferred = '0'
        label = 'Product Line. Property'
        orderIndex = '5'
        required = '1'
        type = 'entry'
      }
      formalParameter 'version', defaultValue: '1.0.0', {
        expansionDeferred = '0'
        label = 'First Release. Version ID'
        orderIndex = '6'
        required = '1'
        type = 'entry'
      }
      formalParameter 'artifacts', defaultValue: 'com.foo:9.1.0, com.bar:5.0.7', {
        expansionDeferred = '0'
        label = 'First Release. Artifacts List'
        orderIndex = '7'
        required = '1'
        type = 'entry'
      }
      formalParameter 'param1', defaultValue: 'foo', {
        expansionDeferred = '0'
        label = 'First Release. Property 1'
        orderIndex = '8'
        required = '1'
        type = 'entry'
      }
      formalParameter 'param2', defaultValue: 'bar', {
        expansionDeferred = '0'
        label = 'First Release. Property 2'
        orderIndex = '9'
        required = '1'
        type = 'entry'
      }
    } // End of catalogItem varCatalogNameNewAppItem
    acl {
      inheriting = '1'
      aclEntry 'group', principalName: varAdminGroupCDServer, {
        changePermissionsPrivilege = 'allow'
        executePrivilege = 'allow'
        modifyPrivilege = 'allow'
        readPrivilege = 'allow'
      }
    }
  } // End of catalog varCatalogNameNewApp
} // End of project varAdminProjectName
