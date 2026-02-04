# set-admin-claim.js — Guide

This guide explains how to run the provided script to set the `admin` custom claim on a Firebase user.

## Prerequisites
- Node.js installed (>= 14 recommended)
- A Firebase project and **service account** JSON (from Project Settings -> Service Accounts -> Generate new private key)
- The service account file must have permissions to manage users (default owner/editor/service account does)

## Setup (Windows)
1. Place the `serviceAccountKey.json` somewhere, e.g. `C:\keys\serviceAccount.json`.
2. Open PowerShell and set the environment variable:

   $env:GOOGLE_APPLICATION_CREDENTIALS = "C:\keys\serviceAccount.json"

3. Install dependencies in the repository (run once):

   npm init -y
   npm install firebase-admin

4. Run the script:

   node scripts/set-admin-claim.js <UID> true

   Example:

   node scripts/set-admin-claim.js u0Xabc123 true

## Setup (Linux / macOS)
1. Export credentials:

   export GOOGLE_APPLICATION_CREDENTIALS="/path/to/serviceAccount.json"

2. Install dependencies:

   npm init -y
   npm install firebase-admin

3. Run the script:

   node scripts/set-admin-claim.js <UID> true

## Notes
- The script revokes refresh tokens after setting claims. Users must sign out/in or call `getIdToken(true)` in client to refresh token and see the `admin` claim.
- After setting the `admin` claim, update Firestore rules accordingly (see `firestore_admin_rules.txt`).

## Example to force refresh token on Android client

FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
  .addOnSuccessListener(result -> {
    Boolean isAdmin = (Boolean) result.getClaims().get("admin");
  });

## Safety
- Only run this script with a service account that you trust. Do not commit the service account JSON to source control.
