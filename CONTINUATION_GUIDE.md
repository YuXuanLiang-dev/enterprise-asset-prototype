# Project Continuation Guide

**Date:** 2025-11-26
**Last Commit:** `feat: implement fiscal asset card import with excel parsing and batch insert`

## Current Status
The project is a Spring Boot (Backend) + Vue.js (Frontend) enterprise asset management system.
We have successfully fixed the **Fiscal Asset Card Import** feature, creating a pattern for handling file imports:
1.  **Backend:** Parse Excel -> Return Preview (JSON).
2.  **Frontend:** Map Headers (Chinese -> English) -> Confirm Import -> Call Batch Insert API.
3.  **Backend:** Persist data via Mapper.

## Immediate Next Steps (Priority High)
The following modules suffer from the same "Unimplemented Import/Mutation" issue as Fiscal Cards and need to be fixed using the established pattern.

### 1. Location Management (`/locations`)
-   **Current State:** Read-only.
-   **Missing Backend Endpoints:**
    -   `POST /locations` (Create)
    -   `PUT /locations/{id}` (Update)
    -   `DELETE /locations` (Delete)
    -   `POST /locations/import` (Batch Insert for Import)
-   **Frontend Action:**
    -   Update `views/Setting/Location/index.vue` to implement `handleConfirmImport` with header mapping.
    -   Connect "Add/Edit/Delete" buttons to new API endpoints.

### 2. Personnel Management (`/departments/personnel`)
-   **Current State:** Read-only (viewing personnel list).
-   **Missing Backend Endpoints:**
    -   `POST /personnel/import` (The "Import Personnel" feature is broken/fake).
    -   CRUD endpoints for Personnel and Departments.
-   **Frontend Action:**
    -   Fix `views/Setting/Department/index.vue` import logic.

### 3. Inventory/Asset Match (`/inventory/match`)
-   **Current State:** The "Import Match Data" feature likely parses but does not save.
-   **Action:** Verify and implement the batch save logic for asset matching.

## Technical Notes & Patterns
-   **Excel Parsing:** The `FileParseController` was updated to use `DataFormatter`. Ensure this is used for all future file parsing to handle numeric/date cells correctly.
-   **Header Mapping:** The frontend is responsible for mapping Chinese Excel headers to English API fields *before* sending data to the `batchInsert` endpoints.
-   **Architecture:** Currently, logic resides in Controllers. If complexity grows, consider extracting business logic into Service classes.

## Useful Commands
-   **Backend Build:** `cd enterprise-asset-backend && ./mvnw clean package -DskipTests`
-   **Frontend Build:** `cd enterprise-salary-platform && npm run build`
