/*
 * get-user-claims.js
 *
 * Usage:
 *  node get-user-claims.js <UID>
 *
 * Requires: firebase-admin (and GOOGLE_APPLICATION_CREDENTIALS set to service account JSON)
 */

const admin = require('firebase-admin');
admin.initializeApp();

async function main() {
  const argv = process.argv.slice(2);
  if (argv.length < 1) {
    console.error('Usage: node get-user-claims.js <UID>');
    process.exit(1);
  }

  const uid = argv[0];
  try {
    const user = await admin.auth().getUser(uid);
    console.log('Custom claims for', uid, ':', user.customClaims);
  } catch (err) {
    console.error('Error fetching user:', err);
    process.exit(2);
  }
}

main();
