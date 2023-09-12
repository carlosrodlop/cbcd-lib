/**
 * Copy and Paste this DSL code into DevOps Essencial DSL Editor in CloudBees CD Server
 * Outputs:
 * - Project to host Admin Resources, including:
 *  - Downstream Release Pipeline
 * - Service Catalog ==> Catalog Item ==> Onboard New Application. Outputs:
 *  - First Upstream Release Pipeline
 *  - Service Catalog ==> Catalog Item ==> New Release for existing Application. Outputs:
 *    - Upstream Release Pipeline consecutive Release
 */

def org='ExampleOrg'
def version='1.0.0'
// Admin Project - Shared Resources
def varAdminProjectName = org + '-ADMIN'
def varAdminGroupCDServer = 'SDAAdmins'
def varDownReleaseName = org + '_DOWNSTREAM_RELEASE_' + version
def varDownPipelineName = 'pipeline_' + varDownReleaseName
def varProcedureCreateUp = org + '_CREATE_UPSTREAM_' + version
// Service Catalog - Templates - Applications Onboarding
def varCatalogNameNewApp = org + '_NEW_APPLICATION_' + version
def varCatalogNameNewAppItem = varCatalogNameNewApp
def varCatalogNameNewRelease = org + '_NEW_RELEASE_' + version
def varCatalogNameNewReleaseItem = varCatalogNameNewRelease
project varAdminProjectName, {
  procedure varProcedureCreateUp, {
    projectName = varAdminProjectName

    formalParameter 'projName', defaultValue: 'ExampleOrg-SDAAdmins-ProductExample', {
      expansionDeferred = '0'
      label = null
      orderIndex = '1'
      required = '1'
      type = 'entry'
    }

    formalParameter 'releaseName', defaultValue: 'ExampleOrg-SDAAdmins-ProductExample-1.1.3', {
      expansionDeferred = '0'
      label = null
      orderIndex = '2'
      required = '1'
      type = 'entry'
    }

    formalParameter 'param1', defaultValue: 'foo', {
      expansionDeferred = '0'
      label = null
      orderIndex = '3'
      required = '1'
      type = 'entry'
    }

    formalParameter 'param2', defaultValue: 'bar', {
      expansionDeferred = '0'
      label = null
      orderIndex = '4'
      required = '1'
      type = 'entry'
    }

    formalParameter 'projectProp', defaultValue: 'ACME', {
      expansionDeferred = '0'
      label = null
      orderIndex = '5'
      required = '1'
      type = 'entry'
    }

    formalParameter 'teamGroup', defaultValue: 'carlos', {
      expansionDeferred = '0'
      label = null
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
        description = \'\'
        actionLabelText = null
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
        } // End Task
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
  }
  // Downstream Pipeline
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
  } // End of Downtream Pipeline
  // Service Catalog - Onboard New Applications
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
      dslString = '''/*******************
Constants (mapping)
********************/
conPreffix = \'''' + org + '''\'
conAdminProject = \'''' + varAdminProjectName + '''\'
conDownstreamRelease = \'''' + varDownReleaseName + '''\'
/*******************
Variables
********************/
def varTeam = args.team
def varTeamDeveloperGroup = args.devGroup
def varTeamReleasePMGroup = args.releasePMGroup
def varProjectName = conPreffix + \'-\' + varTeam + \'-\' + args.product
def varReleaseName = varProjectName + \'-\' + args.version
def varPipelineName = \'pipeline_\' + varReleaseName
def varParam1 = args.param1
def varParam2 = args.param2
def varProjectProp = args.projectProp

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
    param1: varParam1,
    param2: varParam2,
    projectProp: varProjectProp,
    teamGroup: varTeamReleasePMGroup
  ]
)
/*******************
Service Catalog for New Release
********************/
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
/*******************
Constants (mapping)
********************/
conPreffix = \'''' + org + '''\'
conAdminProject = \'''' + varAdminProjectName + '''\'
conDownstreamRelease = \'''' + varDownReleaseName + '''\'
conProjectName = \\\'\'\'\' + varProjectName + \'\'\'\\\'
/*******************
Variables
********************/
def varReleaseName = conProjectName + \\\'-\\\' + args.version
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
    param1: varParam1,
    param2: varParam2,
    projectProp: varProjectProp,
    teamGroup: varTeamReleasePMGroup
  ]
)
\'\'\'
endTargetJson = \'\'
iconUrl = \'icon-process.svg\'
templateObjectType = \'none\'
useFormalParameter = \'1\'
formalParameter \'version\', defaultValue: \'1.1.1\', {
expansionDeferred = \'0\'
label = \'Version ID\'
orderIndex = \'1\'
required = \'1\'
type = \'entry\'
}
formalParameter \'param1\', defaultValue: \'foo\', {
expansionDeferred = \'0\'
label = \'Release Property 1\'
orderIndex = \'2\'
required = \'1\'
type = \'entry\'
}
formalParameter \'param2\', defaultValue: \'bar\', {
expansionDeferred = \'0\'
label = \'Release Property 2\'
orderIndex = \'3\'
required = \'1\'
type = \'entry\'
}
} // End of catalogItem varCatalogNameNewReleaseItem
} // End of catalog varCatalogNameNewRelease
} //End Project'''
      endTargetJson = ''
      iconUrl = 'icon-process.svg'
      templateObjectType = 'none'
      useFormalParameter = '1'
      formalParameter 'team', defaultValue: 'SDAAdmins', {
        expansionDeferred = '0'
        label = 'Team Name'
        orderIndex = '1'
        required = '1'
        type = 'entry'
      }
      formalParameter 'devGroup', defaultValue: 'carlos', {
        expansionDeferred = '0'
        label = 'Developer Group Name'
        orderIndex = '2'
        required = '1'
        type = 'entry'
      }
      formalParameter 'releasePMGroup', defaultValue: 'carlos', {
        expansionDeferred = '0'
        label = 'Release PM Group'
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
      formalParameter 'version', defaultValue: '1.0.0', {
        expansionDeferred = '0'
        label = 'Version ID'
        orderIndex = '5'
        required = '1'
        type = 'entry'
      }
      formalParameter 'projectProp', defaultValue: 'ACME', {
        expansionDeferred = '0'
        label = 'Product Property'
        orderIndex = '6'
        required = '1'
        type = 'entry'
      }
      formalParameter 'param1', defaultValue: 'foo', {
        expansionDeferred = '0'
        label = 'Release Property 1'
        orderIndex = '7'
        required = '1'
        type = 'entry'
      }
      formalParameter 'param2', defaultValue: 'bar', {
        expansionDeferred = '0'
        label = 'Release Property 2'
        orderIndex = '8'
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
} // End of project varProjectName
