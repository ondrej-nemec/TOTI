./gradlew cyclonedxBom
trivy sbom build/reports/cyclonedx-direct/bom.json > build/reports/trivyScan.txt