#! /usr/bin/env groovy
// coveralls.groovy

import groovy.json.*

api = 'https://coveralls.io/api/v1/jobs'

// model for coveralls io report's source file report
class SourceReport {
    String name;
    String source;
    List<Integer> coverage;

    public SourceReport(String name, String source, List<Integer> coverage) {
        this.name = name;
        this.source = source;
        this.coverage = coverage;
    }

}

// model for coveralls io report
class Report {
    String service_job_id;
    String service_name;
    List<SourceReport> source_files;

    public Report() {
    }

    public Report(String serviceJobId, String serviceName, List<SourceReport> sourceFiles) {
        this.service_job_id = serviceJobId;
        this.service_name = serviceName;
        this.source_files = sourceFiles;
    }

    public String toJson() {
        JsonBuilder json = new JsonBuilder()
        json this
        return json.toString()
    }
}

class ReportFactory {
    public static createTest() {
        return new Report('123', 'travis-ci', [new SourceReport('abc', 'def', [1, 2, null])])
    }

    public static createFromCoberturaXML() {
    }
}

def main() {
    def a = ReportFactory.createTest()
    println a.toJson()
}

main()
