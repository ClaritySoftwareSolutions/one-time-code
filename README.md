# one-time-code
A "One Time Code" application that generates and manages the verification of One Time Codes.

Example use case would be as part of an application that uses 2FA authentication as part of its user authentication
process. The application would call the `createOneTimeCode` API that this application exposes and send the generated code
to the user's other device (email, SMS etc). As part of the authentication flow the user is then prompted to enter the 
code that they've been sent. The application captures this value and sends it to the `validateOneTimeCode` API to validate
the code and complete authentication.

## Exposed APIs
### REST API
Refer to the [openApi spec](https://github.com/ClaritySoftwareSolutions/one-time-code/blob/main/src/main/resources/openapi/OneTimeCode.yml) for details.

### Java API
TBD, but the `domain` package could be used by another application to call and use the One Time Code functionality directly. 
In theory there is no need to use the exposed REST API

## Software Design & Architecture
The application architecture is based on a [hexagonal](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)) style design.
