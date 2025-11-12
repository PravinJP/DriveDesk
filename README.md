## 📝 Description

**DriveDesk** is a unified placement assistance platform built to streamline and modernize campus placement coordination and communication.  
This repository contains the **backend implementation** developed using **Spring Boot**, which serves as the core engine for managing user roles, job postings, assessments, and proctoring functionalities.

Traditional placement processes often rely on sharing job details and updates through messaging apps or emails, leading to scattered communication, missed opportunities, and inefficiencies.  
**DriveDesk** solves this problem by providing a **centralized system** where all stakeholders — admins, teachers, and students — can interact in a structured and transparent environment.

The backend offers a **role-based authentication model** that defines clear privileges for each user:
- **Admin:** Creates and manages login credentials for teachers and students.  
- **Teacher:** Posts job openings, conducts coding assessments, and tracks student participation.  
- **Student:** Views job opportunities, expresses interest in drives, and participates in assessments within the platform.

A robust **Job Alert Management System** allows teachers to create detailed job posts with eligibility criteria and descriptions, which are automatically displayed on student dashboards. Students can then register their interest with a single click, ensuring every placement opportunity is easily accessible.

The backend also powers an **Assessment Module**, where teachers can create and assign coding tests.  
These assessments include:
- Problem descriptions and constraints.  
- **Automated code evaluation** based on predefined test cases.  
- Instant feedback for students upon submission.

To ensure fairness and integrity, **proctoring measures** such as **camera monitoring** and **tab-switch detection** are supported during assessments.

By centralizing communication, automating evaluation, and enabling transparent interaction between placement coordinators and students, **DriveDesk** significantly improves efficiency and reduces the administrative burden of managing campus drives.

Overall, this backend ensures:
- Secure and scalable API endpoints for data management.  
- Structured flow for placement activities.  
- Seamless integration with a React.js frontend and a PostgreSQL database.  

**DriveDesk** ultimately bridges the gap between placement coordinators and students — bringing organization, automation, and reliability to the campus recruitment process.
