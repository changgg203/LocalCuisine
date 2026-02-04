/*
 * set-admin-claim.js
 *
 * Usage:
 *  node set-admin-claim.js <UID> [true|false]
 *
 * Requirements:
 * - Node.js
 * - npm install firebase-admin
 * - A Firebase service account JSON file (from Firebase Console)
 * - Set environment variable GOOGLE_APPLICATION_CREDENTIALS pointing to the JSON file OR
 *   place the JSON in the same folder and edit the code to require it directly.
 *
 * This script sets custom claim `admin` on the specified user.
 */

const admin = require('firebase-admin');
const path = require('path');

// If you prefer to pass service account explicitly, uncomment and adjust below:
// const serviceAccount = require(path.join(__dirname, 'serviceAccountKey.json'));
// admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });

// Initialize using default credentials (GOOGLE_APPLICATION_CREDENTIALS env variable)
admin.initializeApp();

async function main() {
  const argv = process.argv.slice(2);
  if (argv.length < 1) {
    console.error('Usage: node set-admin-claim.js <UID> [true|false]');
    process.exit(1);
  }

  const uid = argv[0];
  const value = argv[1] === undefined ? 'true' : argv[1];
  const isAdmin = value === 'true' || value === '1' || value === 'yes';

  try {
    console.log(`Setting admin=${isAdmin} on user: ${uid}`);
    await admin.auth().setCustomUserClaims(uid, { admin: isAdmin });

    // Optionally revoke refresh tokens so user needs to fetch new token
    // (useful to force token refresh immediately)
    console.log('Revoking refresh tokens for the user to force token refresh...');
    await admin.auth().revokeRefreshTokens(uid);

    // Read back claims
    const user = await admin.auth().getUser(uid);
    console.log('Custom claims:', user.customClaims);

    console.log(`Done. If the user is logged in on device, ask them to sign out and sign in again or call getIdToken(true) to refresh tokens.`);
  } catch (err) {
    console.error('Error setting custom claim:', err);
    process.exit(2);
  }
}

main();
