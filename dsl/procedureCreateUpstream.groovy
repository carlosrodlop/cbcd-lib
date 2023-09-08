procedure 'Create Upstream', {
  description = ''
  jobNameTemplate = ''
  projectName = 'ExampleOrg-ADMIN'
  resourceName = ''
  timeLimit = '0'
  timeLimitUnits = 'minutes'
  workspaceName = ''

  formalParameter 'projectName', defaultValue: 'ExampleOrg-SDAAdmins-ProductExample', {
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

  formalParameter 'pipelineName', defaultValue: 'test', {
    expansionDeferred = '0'
    label = null
    orderIndex = '7'
    required = '1'
    type = 'entry'
  }

  step 'Create Upstream', {
    description = ''
    alwaysRun = '0'
    broadcast = '0'
    command = '''varTeamGroup = "$[teamGroup]"
varProjectName = "$[projectName]"
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
}//End Release'''
    condition = ''
    errorHandling = 'failProcedure'
    exclusiveMode = 'none'
    logFileName = ''
    parallel = '0'
    postProcessor = ''
    precondition = ''
    releaseMode = 'none'
    resourceName = ''
    shell = 'ectool evalDsl --dslFile \'{0}\''
    subprocedure = null
    subproject = null
    timeLimit = '0'
    timeLimitUnits = 'seconds'
    workingDirectory = ''
    workspaceName = ''
  }
}
