## Overview

Reporting is a standard feature of enterprise applications. Organizations have a constant need to extract useful information from their database in the form of reports. These reports provide an overview or a detailed picture of certain aspects of the overall business and serve as primary guide for decision making. Manual preparation of report is usually a laborious, tedious, time-consuming process. Computer generated reports eliminate this and significantly cuts down report production time from days to a few seconds or minutes.

Reports have different forms and may contain information presented as text in tabular layout, graphical information as charts or images, and even sub-reports. They can also be produced in various file formats depending on who or what the intended report consumer is. Common formats include the Adobe Portable Document Format (PDF), the Microsoft Word and Excel formats (DOC, XLS), Comma-separated format (CSV) and HTML format. Reports can also be of different page sizes and orientation which include portrait or landscape.

<img src="images/corecomponents/reportingcomponents.png" alt="Reporting Components" width="484" align="center">

Figure 1.0 Reporting components
 
Unify framework makes available to your application report generation capabilities that are flexible enough to accommodate these variations. It provides a simple report object model with the _Report_ class and a component for generating reports with the _ReportServer_ interface.

The report generation framework allows us to compose and generate reports which use static templates or dynamic reports which are designed on the fly.

## Composing a Report

You compose a report using a _Report.Builder_ object which you obtain from the static _Report.newBuilder()_ method. The builder object allows you specify the various properties of your report including the report type, the report layout,
the report file format, the report size and orientation, and of course in some cases, the report datasource. Below are steps for preparing a _Report_ object in no particular order.

* Set the report title using the _title()_ method passing a _String_. The report title is usually displayed in a large font on the header of the first page of the report.
* Set the report file format using the _format()_ method passing a _ReportFormat_ object. The default value of this property is _ReportFormat.PDF_.
* Use the _layout()_ method to set the name of the _ReportLayoutManager_ component to be used for rendering the report. Applies only to dynamic reports. The default value is _ReportLayoutManagerConstants.TABULAR_REPORTLAYOUTMANAGER_.
* Use the _query()_ method to set a native query used to fetch data from a datasource to populate your report.
* Use the _table()_ method to set the primary table of your dynamic report when your data is from a datasource.
* Use any of the _addColumn()_ methods to specify report columns when you are building a dynamic report.
* Use any of the _addJoin()_ methods to specify table joins to your datasource query when you are building a dynamic report. 
* Use the _beginCompoundFilter()_, _endCompoundFilter()- and any of the _addSimpleFilter()_ methods to add column filters to your datasource query when you are building a dynamic report. Filters basically specify conditions in a WHERE clause attached to the datasoure query during report generation.
* Use any of the _setParameter()_ methods to add a named parameter to you report. You can make multiple calls to add more than one parameter.
* Set the report data source using the _dataSource()_ method passing the name of the _DataSource_ component. This is required if your report is to be populated from a database. The datasource can be any of the ones configured in the container.
* Set the report bean collection calling the _beanCollection()_ method and passing your collection of bean data objects. This is required if your report is to be populated from a java bean collection.
* Use the _template()_ method to set the filename of the report template. A report template is a file that contains structure information and static data used by a report engine to render a set of data on a report. The format of a report template file is specific to the implementation of the _ReportServer_ that will be used in generating the report. A template is always required for regular static reports and may be set for dynamic reports that need some underlying static design.
* Use the _processor()_ method to set the component name of a _ReportProcessor_ component whose _process()_ method would be applied to the _Report_ object just before report generation.
* Use the _landscape()_ method to set the report orientation to landscape. The default value is _false_ which corresponds to a default portrait orientation. Applies only to dynamic reports.
* Use the _shadeOddRows()_ method to shade alternate rows in generated report. Defaults to _false_ and applies only to dynamic reports.
* Use the _underlineRows()- method to display an underline for every row in generated report. Defaults to _false_ and applies only to dynamic reports.

### Composing a Static Report using Datasource

Listing 1: Composing a static report with datasource

```java
//Composing a static report with data from datasource
Report report = Report.newBuilder()
    .title("Outward Cheque Details")
    .format(ReportFormat.PDF)
    .template("c:\\templates\\outward_cheque_dtl.tmpl")
    .dataSource("application-datasource")
    .build();
```

### Composing a Static Report using Bean Collection

Listing 2: Composing a static report using bean collection

```java
//Composing a static report with data from bean collection
List<InwardChequeSummary> inwardChequeSummaryList = ...

Report report = Report.newBuilder()
    .title("Inward Cheque Summary")
    .format(ReportFormat.XLS)
    .template("f:\\inward_cheque_summ.tmpl")
    .beanCollection(inwardChequeSummaryList)
    .build();
```

### Composing a Dynamic Report using Datasource

Listing 3: Composing a dynamic report using datasource

```java
//Buiding a dynamic report with a datasource and query
Report report = Report.newBuilder()
    .title("Branch")
    .format(ReportFormat.PDF)
    .layout(ReportLayoutManagerConstants.TABULAR_REPORTLAYOUTMANAGER)
    .query("SELECT BRANCH_CD, BRANCH_NM, ROUTING_NO, REC_ST FROM BRANCH")
    .dataSource("application-datasource")
    .addColumn("Code", "BRANCH_CD", String.class, 10)
    .addColumn("Name", "BRANCH_NM", String.class, 20)
    .addColumn("Routing No.", "ROUTING_NO", String.class, 10)
    .addColumn("Status", "REC_ST", String.class, 10)
    .shadeOddRows(true) // Add alternating row shading
    .underlineRows(true) // Underline rows
    .landscape(true) //Report with landscape orientation
    .build();
```

### Composing a Dynamic Report using Bean Collection

Listing 4: Composing a dynamic report using bean collection

```java
//Building a dynamic report with a bean collection
List<Account>accountList = ...
Report report = Report.newBuilder()
    .title("Accounts")
    .format(ReportFormat.CSV)
    .beanCollection(accountList)
    .template("background.jrxml")
    .addColumn("Account", "accountNo", String.class, 10)
    .addColumn("Customer Name", "customerName", String.class, 20)
    .addColumn("Available Bal.", "availBalance", BigDecimal.class, 10)
    .addColumn("Status", "status", String.class, 10)
    .build();
```

## Report Pre-processing

The _ReportProcessor_ component is used to perform some pre-processing on a _Report_ object. It is typically used to enrich or manipulate report parameters and other mutable properties of the _Report- object based on some logic.
If a _ReportProcessor_ is associated with a _Report_ object, it is executed in the _ReportServer_ component just before actual report generation.

The framework provides two convenient abstract base classes - _AbstractReportProcessor_ and _AbstractBeanCollectionReportProcessor_ - which you can extend to implement a _ReportProcessor_.

### Implementing a Report Processor

Listing 5: Report processor example

```java
@Component("monthlysales-reportprocessor")
public class MonthlySalesReportProcessor extends AbstractReportProcessor {

    private static final String HEADOFFICE_BRANCHCODE = "100";
    
    @Configurable
    private OrganizationService organizationService;
    
    @Override
    public void process(Report report) throws UnifyException {
        String branchCode = (String) report.getParameter("branchCode");
        if (branchCode == null) {
            // Default to head office is branch code is not supplied
            branchCode = HEADOFFICE_BRANCHCODE;
        }
        
        // Perform enrichment. Add branch details to report.
        Branch branch = organizationService.getBranch(branchCode);
        report.setParameter("branchCode ", branchCode);
        report.setParameter("branchDesc", branch.getDescription());
        report.setParameter("branchSortCode", branch.getSortCode());
    }
}
```

## Generating a Report

The _ReportServer_ component is used for generating reports. It defines a set of overloaded report generation methods that accept a _Report_ object and an output target. The output target can be:
1. a file on a file system that is accessible by the container.
2. an OutputStream object.

### Generating a Report to file

Listing 6: Generating a report to file

```java
// Compose report
Report summaryReport = ...

// Get handle to a report server
ReportServer reportServer = ...

//Generate report to file by file name
reportServer.generateReport(summaryReport,
    "C:\\reports\\OutwardSummaryByBank.pdf");

//Generate report to file by file object
File file = new File("E:\\archive\\OutwardSummaryByBank.pdf");
reportServer.generateReport(summaryReport, file);
```

### Generating a Report to OutputStream

Listing 7: Generating a report to OutputStream

```java
// Compose report
Report summaryReport = ...

// Get handle to a report server
ReportServer reportServer = ...

//Generate report to output stream
OutputStream outputStream = ...
reportServer.generateReport(summaryReport, outputStream);
```