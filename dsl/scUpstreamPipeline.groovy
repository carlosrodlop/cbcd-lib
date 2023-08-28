catalogItem 'ORG_ONBOARD_NEW_APPLICATION_1.0.0', {
  description = '''<xml>
  <title>
    ORG ONBOARD NEW APPLICATION VERSION 1.0.0
  </title>

  <htmlData>
    <![CDATA[
      
    ]]>
  </htmlData>
</xml>'''
  buttonLabel = 'Create'
  catalogName = 'ORG_ONBOARD_NEW_APPLICATION'
  dslString = '''// Constants

def conPreffix = \'ORG\'
def conAdminProject = \'ORG_ADMIN\'
def conDownstreamRelease = \'ORG_DOWNSTREAM_RELEASE_1.0.0\'

// Variables

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
  
  // Custom properties
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

  release varReleaseName, {
  
    projectName = varProjectName

   	pipeline varPipelineName, {
    
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

      aclEntry \'user\', principalName: varTeamDeveloperGroup, {
        changePermissionsPrivilege = \'inherit\'
        executePrivilege = \'allow\'
        modifyPrivilege = \'inherit\'
        readPrivilege = \'inherit\'
      }
  	}//End ACL Release
   }//End Release
} //End Project'''
  iconUrl = 'icon-process.svg'
  projectName = 'ORG_ADMIN'
  useFormalParameter = '1'

  formalParameter 'team', defaultValue: 'SDAAdmins', {
    label = 'Team Name'
    orderIndex = '1'
    required = '1'
    type = 'entry'
  }

  formalParameter 'devGroup', defaultValue: 'carlos', {
    label = 'Developer Group Name'
    orderIndex = '2'
    required = '1'
    type = 'entry'
  }

  formalParameter 'releasePMGroup', defaultValue: 'carlos', {
    label = 'Release PM Group'
    orderIndex = '3'
    required = '1'
    type = 'entry'
  }

  formalParameter 'product', defaultValue: 'ProductExample', {
    label = 'Product Line'
    orderIndex = '4'
    required = '1'
    type = 'entry'
  }

  formalParameter 'version', defaultValue: '1.0.0', {
    label = 'Version ID'
    orderIndex = '5'
    required = '1'
    type = 'entry'
  }

  formalParameter 'projectProp', defaultValue: 'ACME', {
    label = 'Product Property'
    orderIndex = '6'
    required = '1'
    type = 'entry'
  }

  formalParameter 'param1', defaultValue: 'foo', {
    label = 'Release Property 1'
    orderIndex = '7'
    required = '1'
    type = 'entry'
  }

  formalParameter 'param2', defaultValue: 'bar', {
    label = 'Release Property 2'
    orderIndex = '8'
    required = '1'
    type = 'entry'
  }

}