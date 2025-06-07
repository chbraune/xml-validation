# XML Validation Service

## Introduction

The XML Validation Service is a example Spring Boot application designed to validate XML documents, with a specialized focus on SPOT XML example format. This service helps ensure data integrity by validating XML files against predefined XSD schemas, making it ideal for systems that need to verify the structure and content of XML documents before processing them.

Key capabilities include:
- Individual validation of SPOT elements within XML documents
- Detailed error reporting with exact position and reason for validation failures
- RESTful API interface for easy integration into existing systems
- Support for custom XSD schema validation

Whether you're dealing with batch processing of XML files or need real-time validation of XML data, this service provides a reliable and efficient solution with clear error reporting and easy integration options.


#### Parameters

- `xmlPath`: Path to the XML file to validate
- `xsdPath`: Path to the XSD schema file

#### Responses

- `200 OK`: XML is valid
- `400 Bad Request`: XML is invalid (includes detailed validation errors)

``` http request
POST /api/validation/spot Content-Type: application/x-www-form-urlencoded
```
## Building the Project

The project uses Gradle as its build system. To build the project:

```shell
./gradlew build
```

## Running Tests

To run the tests:

```shell
./gradlew test
```

The test suite includes various scenarios:
- Validation of valid XML files
- Validation of invalid XML files
- Error handling for missing parameters
- Schema validation errors

## Dependencies

- spring-boot-starter-web
- jakarta.xml.bind-api
- spring-boot-starter-test (for testing)

## Project Structure

- `controller/`: Contains REST API endpoints
- `service/`: Contains business logic for XML validation
- `resources/`: Contains test XML and XSD files

## Error Handling

The service provides detailed error messages including:
- Position references in the XML
- Specific validation error types
- Detailed error messages for schema violations

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the Apache License 2.0 

Copyright 2024 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
