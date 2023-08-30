/**
 * This is a DSL script to create an Upstream Release Pipeline calling a Downstream Release Pipeline.
 * Outputs:
 * - Project to host Admin Resources
 * - Downstream Release Pipeline (Admin Full Permissions, Read Permissions of the Rest)
 * - Service Catalog ==> Catalog Item ==> Onboard New Application
 * - Service Catalog ==> Catalog Item ==> New Release for existing Application 
 */

org='Example'
def version='1.0.0'
def varProjectName = org + '-ADMIN'
def varAdminGroupCDServer = 'SDAAdmins'
def varDownReleaseName = org + '_DOWNSTREAM_RELEASE_' + version
def varDownPipelineName = 'pipeline_' + varDownReleaseName
def varCatalogNameNewApp = org + '_NEW_APPLICATION' + version
def varCatalogNameNewAppItem = varCatalogNameNewApp

project varProjectName, {
  // Downstream Pipeline
  release varDownReleaseName, {
    projectName = varProjectName
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
      stage 'Stage 1', {
        pipelineName = varDownPipelineName
        task 'echo', {
          command = '''echo "Param 1:   $[param1]"
echo "Param 2:  $[param2]"'''
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
    projectName = varProjectName
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
conAdminProject = \'''' + varProjectName + '''\'
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
// Setter properties to new Project
def varProjectProp = args.projectProp
/*******************
Create Release Pipeline
********************/
def createReleasePipeline(varReleaseName, varProjectName, varParam1, varParam2, varPipelineName, varTeamGroup){
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
stage \'Stage 1\', {
pipelineName = varPipelineName
task \'Call downstream\', {
description = \'\'
actionLabelText = null
actualParameter = [
\'param1\': \'$[param1]\',
\'param2\': \'$[param2]\',
]
errorHandling = \'stopOnError\'
subErrorHandling = \'continueOnError\'
subproject = conAdminProject
subrelease = conDownstreamRelease
taskType = \'RELEASE\'
triggerType = \'async\'
} // End Task
}//End Stage
}//End Pipeline
subrelease {
subreleaseName = conDownstreamRelease
subreleaseProject = conAdminProject
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
}//End Release
}
/*******************
Main
********************/
project varProjectName, {
// Common properties for Team - Application
ProjectProp1 = varProjectProp
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
createReleasePipeline(varReleaseName, varProjectName, varParam1, varParam2, varPipelineName, varTeamReleasePMGroup)
//TODO: Create Promotion Pipeline
} //End Project'''
      endTargetJson = ''
      iconUrl = 'icon-process.svg'
      subpluginKey = null
      subprocedure = null
      subproject = null
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