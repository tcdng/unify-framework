## Overview

A task is a unit of programming that is executed in a separate thread. The framework defines a set of classes that allows you to manage the execution of tasks in your web application. This is shown in the figure below.

<img src="images/corecomponents/taskcomponents.png" alt="Task Components" width="600" align="center">

Figure 1.0 Task components
 
Execution of tasks is managed by the _TaskManager_ component. It provides and manages the various methods by which tasks can be execute. Tasks can be setup to run once, a number of times based on a certain period and continuously in a periodic manner. Tasks can start running immediately or after a specified delay period. Each of the task execution methods allow you to pass a set of parameters that a _Task_ might require to run. 

Methods that allow you to group tasks together to run in the same thread in a dependent or independent sequence are also available. A dependent sequence means all tasks in the sequence must run successfully without an exception otherwise all subsequent tasks from the failing task would be aborted. All tasks in an independent sequence would be executed irrespective of any task in the sequence failing. 

Every time the _TaskManager_ prepares a task for execution, a _TaskMonitor_ object is created for that instance of execution and is returned to the calling process. Such a process, can use the shared _TaskMonitor_ object to observe the state of the task as it runs. The _TaskMonitor_ object can also be used by the observing process to exchange messages between itself and the executing task.

A _TaskStatusLogger_ component is used by a _Task_ to log status information as it runs through its different states of execution. You can specify the name of a _TaskStatusLogger_ component for your task when you invoke any of the _TaskManager_ task execution methods. Using a _TaskStatusLogger_ is optional as a task may not require any form of status logging.

Although the task execution methods of a _TaskManager_ component can be accessed directly, we recommend that you use the _TaskLauncher_ component for setting up tasks for execution. It provides a simpler API through a _launchTask()_ method that accepts a _TaskSetup_ object which you construct in a fluent manner.

## Implementing a Task

The framework provides two ways of defining a task which are:
1. By implementing a Task component.
2. By making a business service’s public method taskable.

Any of these two methods can be used based on convenience or design requirements. Tasks are setup for and executed the same way irrespective which method is used for implementation.

### Implementing a Task Component
All task components must implement the _com.tcdng.unify.core.task.Task_ interface.

The _TaskManager_ component runs a _Task_ instance by invoking the _execute()_ method, passing a _TaskMonitor_ object, a _TaskInput_ object and a _TaskOutput_ object.
Any implementation of the _execute()_ method should observe the _TaskMonitor_ object to know if it has received an external message; for example a _stop()_ or _cancel()_ command. This is especially important for long-running tasks that can be canceled before completion. Use the _TaskMonitor_ _addMessage()_ method to relay real-time information of the running task’s progress to the monitoring process.
Get execution input parameters from the _TaskInput object_ and write any expected output to the _TaskOutput_ object.

The framework provides a convenient _AbstractTask_ class that you can extend.


Listing 1. ReportGenerationTask.java

```java
@Component("reportgeneration-task")
public class ReportGenerationTask extends AbstractTask {

    public static final String REPORT_TO_GENERATE = "REPORT_TO_GENERATE";
    public static final String REPORT_FILENAME = "REPORT_FILENAME";

    public static final String SUCCESS_INDICATOR = "SUCCESS_INDICATOR";

    @Configurable
    private ReportServer reportServer;

    @Override
    public void execute(TaskMonitor taskMonitor, TaskInput taskInput,
        TaskOutput taskOutput) throws UnifyException {
        Report report = taskInput.getParam(Report.class, REPORT_TO_GENERATE);
        String filename = taskInput.getParam(String.class, REPORT_FILENAME);

        FileOutputStream fos = null;
        try {
            //Open file to write report to
            fos = new FileOutputStream(filename);

            // Generate report
            reportServer.generateReport(report, fos);
        } catch (FileNotFoundException e) {
            throwOperationErrorException(e);
        } finally {
            IOUtils.close(fos);
        }

        taskOutput.setResult(REPORT_FILENAME, filename);
        taskOutput.setResult(SUCCESS_INDICATOR, Boolean.TRUE);
    }   
}
```

### Implementing a Taskable Method

Implement a taskable method by annotating a compliant _BusinessService_ component method with the _@Taskable_ annotation.
Compliant methods have the requirements outlined below.
1. A public access modifier.
2. A non-void return type.
3. A least one parameter and the first parameter must be of type _TaskMonitor_.

Listing 2. Taskable method

```java
@Taskable(name = "amortizationcalculation-task",
    description = "Amortization Calculation Task",
    parameters = {
        @Parameter(name = "PRINCIPAL", type = BigDecimal.class),
        @Parameter(name = "RATE", type = Double.class),
        @Parameter(name = "TERM_MONTHS", type = Integer.class) },
    limit = TaskExecLimit.ALLOW_MULTIPLE)
public AmortizationSchedule calculateAmortization(TaskMonitor taskMonitor,
    BigDecimal principal, Double rate, Integer termInMonths) throws UnifyException {
    // Calculate amortization schedule using arguments
    AmortizationSchedule amortizationSchedule = ...
    
    // Return task result
    return amortizationSchedule;
}
```

## Executing Tasks

Setting up a task for execution using the _TaskLauncher_ component requires you to first compose a _TaskSetup_ object and then pass it as a parameter to an invocation of the task launcher’s _launchTask()_ method.  Depending on how you compose the _TaskSetup_ object you can execute a single task, or multiple tasks in sequence, immediately, after a delay or periodically.

### Executing Task Immediately
Here immediate execution means setting up a task or sequence of tasks to run without any initial delay. The exact time execution begins is ultimately determined by the Java platform thread scheduling mechanism which the _TaskManager_ component depends on.

Listing 3. Execute immediate task

```java
// Get task launcher
TaskLauncher taskLauncher
    = (TaskLauncher) getComponent(ApplicationComponents.APPLICATION_TASKLAUNCHER);

// Perform task setup
Report report = ...
TaskSetup taskSetup = TaskSetup.newBuilder(TaskExecType.RUN_IMMEDIATE)
    .addTask("reportgeneration-task")
    .setParam("REPORT_TO_GENERATE", report)
    .setParam("REPORT_FILENAME", "C:\\reports\\DailyAccruals_20191205.pdf")
    .build();

// Launch task
TaskMonitor taskMonitor = taskLauncher.launchTask(taskSetup);

// Pass monitor to some observing process
```

### Scheduling Task to Run after Delay

You can schedule a task or a sequence of tasks to run once after a specified delay or at some specified time in the future.

Listing 4. Execute task after delay

```java
// Get task launcher
TaskLauncher taskLauncher
    = (TaskLauncher) getComponent(ApplicationComponents.APPLICATION_TASKLAUNCHER);

// Compose delayed task setup
TaskSetup taskSetup = TaskSetup.newBuilder(TaskExecType.RUN_AFTER)
    .addTask("batchuploadtask")
    .setParam("filename", "premiums.csv")
    .delayInMillSec(30 * 60 * 1000) // 30 minutes
    .build();

// Launch task (does not block)
TaskMonitor taskMonitor = taskLauncher.launchTask(taskSetup);

// Pass monitor to some task observing process
```

Listing 5. Execute task at specific time

```java
// Get task launcher
TaskLauncher taskLauncher
    = (TaskLauncher) getComponent(ApplicationComponents.APPLICATION_TASKLAUNCHER);

// Compose delayed task setup
Calendar cal = Calendar.getInstance();
cal.set(Calendar.MONTH, Calendar.DECEMBER);
cal.set(Calendar.DAY_OF_MONTH, 31);
cal.set(Calendar.HOUR_OF_DAY, 5);
cal.set(Calendar.MINUTE, 0);

TaskSetup taskSetup = TaskSetup.newBuilder(TaskExecType.RUN_AFTER)
    .addTask("endOfYearReportTask")
    .delayUntil(cal.getTime()) // December 31st, 5:00AM
    .build();

// Launch task (does not block)
taskLauncher.launchTask(taskSetup);
```

### Scheduling Tasks to Run Periodically

A task or a sequence of tasks can be setup to run periodically for a number of times or indefinitely. Periodic tasks will always run again, within the number of times restriction, irrespective of the results of the previous run.

Listing 5. Schedule task to run periodically and indefinitely

```java
// Get task launcher
TaskLauncher taskLauncher
    = (TaskLauncher) getComponent(ApplicationComponents.APPLICATION_TASKLAUNCHER);

// Compose periodic (indefinite) task setup
TaskSetup taskSetup = TaskSetup.newBuilder(TaskExecType.RUN_PERIODIC)
    .addTask("processremotefiletask")
    .setParam("branchCode", "011")
    .periodInMillSec(60 * 1000) // Every 1 minute
    .build();

// Launch task
taskLauncher.launchTask(taskSetup);
```

Listing 6. Schedule task to run periodically a number of times

```java
// Get task launcher
TaskLauncher taskLauncher
    = (TaskLauncher) getComponent(ApplicationComponents.APPLICATION_TASKLAUNCHER);

// Compose periodic (limited) task setup
Calendar cal = Calendar.getInstance();
cal.set(Calendar.HOUR_OF_DAY, 9);
cal.set(Calendar.MINUTE, 0);
cal.set(Calendar.SECOND, 0);
TaskSetup taskSetup = TaskSetup.newBuilder(TaskExecType.RUN_PERIODIC)
    .addTask("processremotefiletask")
    .setParam("branchCode", "011")
    .delayUntil(cal.getTime()) // Start at 9:00AM
    .periodInMillSec(60 * 1000) // Every 1 minute
    .numberOfTimes(20) //20 times
    .build();

// Launch task does not block
taskLauncher.launchTask(taskSetup);
```