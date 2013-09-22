#! /usr/bin/env groovy
// coveralls.groovy

import groovy.json.*

API = 'https://coveralls.io/api/v1/jobs'

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

    public Report(String serviceName, String serviceJobId, List<SourceReport> sourceFiles) {
        this.service_name = serviceName;
        this.service_job_id = serviceJobId;
        this.source_files = sourceFiles;
    }

    public String toJson() {
        JsonBuilder json = new JsonBuilder()
        json this
        return json.toString()
    }
}

class SourceReportFactory {

    public static List<SourceReport> createFromCoberturaXML(File file) {
        Node coverage = new XmlParser().parse(file)
        String sourceDir = coverage.sources.source.text() + '/'

        Map a = [:]

        coverage.packages.package.classes.class.each() {
            Map cov = a.get(it.'@filename', [:])

            it.lines.line.each() {
                cov[it.'@number'.toInteger()] = it.'@hits'.toInteger()
            }
        }

        List<SourceReport> reports = new ArrayList<SourceReport>()

        a.each { String filename, Map cov ->
            def max = cov.max { it.key }

            List r = [null] * max.key
            cov.each { Integer line, Integer hits ->
                r[line] = hits
            }

            reports.add new SourceReport(filename, new File(sourceDir + filename).text, r)
        }

        return reports

    }
}

// model for ci service info
class ServiceInfo {
    String serviceName;
    String serviceJobId;

    public ServiceInfo(String serviceName, String serviceJobId) {
        this.serviceName = serviceName;
        this.serviceJobId = serviceJobId;
    }
}

class ServiceInfoFactory {

    public static createFromEnvVar() {

        if (System.getenv('TRAVIS') == 'true') {
            return new ServiceInfo('travis-ci', System.getenv('TRAVIS_JOB_ID'))
        }

        // cannot create service info from env var
        return null

    }

}

def main() {
    ServiceInfo serviceInfo = ServiceInfoFactory.createFromEnvVar()

    List<SourceReport> sourceReports = SourceReportFactory.createFromCoberturaXML new File('sample.xml')

    Report rep = new Report(serviceInfo.serviceName, serviceInfo.serviceJobId, sourceReports)

    println rep.toJson()
}

main()
