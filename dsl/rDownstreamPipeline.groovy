release 'ORG_DOWNSTREAM_RELEASE_1.0.0', {
  disableMultipleActiveRuns = '0'
  projectName = 'ORG_ADMIN'

  pipeline 'pipeline_ORG_UPSTREAM_RELEASE', {
    releaseName = 'ORG_DOWNSTREAM_RELEASE_1.0.0'
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

    formalParameter 'ec_stagesToRun', defaultValue: null, {
      expansionDeferred = '1'
      label = null
      orderIndex = null
      required = '0'
      type = null
    }

    stage 'Stage 1', {
      completionType = 'auto'
      pipelineName = 'pipeline_ORG_UPSTREAM_RELEASE'

      task 'echo', {
        command = '''echo "Param 1:   $[param1]"
echo "Param 2:  $[param2]"'''
        enabled = '1'
        errorHandling = 'stopOnError'
        insertRollingDeployManualStep = '0'
        resourceName = ''
        skippable = '0'
        taskType = 'COMMAND'
        useApproverAcl = '0'
        waitForPlannedStartDate = '0'
      }

      task 'Project Properties', {
        command = '''ectool setProperty \'/projects/$[/myPipelineRuntime/projectName]/Myprop\' \'value\'

'''
        enabled = '1'
        errorHandling = 'stopOnError'
        insertRollingDeployManualStep = '0'
        resourceName = ''
        skippable = '0'
        taskType = 'COMMAND'
        useApproverAcl = '0'
        waitForPlannedStartDate = '0'
      }
    }

    stage 'Stage 2', {
      colorCode = '#ff7f0e'
      completionType = 'auto'
      pipelineName = 'pipeline_ORG_UPSTREAM_RELEASE'
    }

    stage 'Stage 3', {
      colorCode = '#2ca02c'
      completionType = 'auto'
      pipelineName = 'pipeline_ORG_UPSTREAM_RELEASE'
    }

    // Custom properties
    property 'ec_counters', {
      // Custom properties
      pipelineCounter = '7'
    }
  }
}