### Test Case Specification: [TEST CASE NAME]
**Created** : [DATE]  **Spec/Plan Link** : [link]  **Status** : [Draft / In Process / Approved]

#### Test Overview  *(mandatory)*
<!--
IMPORTANT: Each test case must clearly state its scope, target system version,
and ownership details. Keep metadata clear and technology-agnostic where appropriate.
-->

**Test Case ID** : [e.g., TC-001-FeatureName]
**Product Name & Version** : [Product Name vX.Y]
**Author** : [Author Name]
**Current Owner** : [Owner / QA Lead]
**Priority** : [P1 - Critical / P2 - High / P3 - Medium]

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*
<!--
ACTION REQUIRED: Describe the primary verification objective and system scope.
-->

##### Test Objectives
This test case verifies that [specific capability / user journey] functions successfully under specified operational conditions.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| [Feature Spec] | [Specification Document] | [specs/feature/spec.md] |
| [Implementation Plan] | [Plan Document] | [specs/feature/plan.md] |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* [Describe hardware, network, browser, or environment limitations, or "None"]

##### Dependencies & Assumptions
* [Prerequisite test cases that must pass first, e.g., "TC-000 User Registration must pass"]
* [System assumptions or background states]

##### Preconditions & Default Setup
* **Initial State** : [e.g., User is logged out, database contains seed data]
* **Environment** : [e.g., Staging / Sandbox environment]
* **Test Accounts** : [e.g., Admin user: admin@test.com]

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*
<!--
ACTION REQUIRED: Fill out the step-by-step process flow table below.
Add or remove sections and steps as needed for the scenario.
-->

##### Section 1: [Primary Functional Journey Name]
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | [e.g., Navigate to login page] | [Open browser and go to /login] | [URL] | [Login form is displayed correctly] | [ ] | [ ] |
| 2 | [e.g., Enter valid credentials] | [Fill email and password, click 'Submit'] | [user@test.com / Pass123!] | [User redirected to dashboard] | [ ] | [ ] |
| 3 | [e.g., Verify session state] | [Check user profile header] | [N/A] | [Header displays active user profile] | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: [Secondary Flow / Error Handling]
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | [e.g., Trigger invalid input] | [Enter invalid email format] | [invalid-email] | [Inline validation error shown] | [ ] | [ ] |
| 2 | [e.g., Attempt login] | [Click 'Submit' with invalid data] | [N/A] | [Form submission blocked] | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : What happens when network connection is dropped during step [X]?
* **EC-002** : How does system handle concurrent logins with the same account?

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
