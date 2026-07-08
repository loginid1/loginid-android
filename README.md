# loginid-android

Monorepo for LoginID's Android SDKs. This repository contains native Kotlin SDKs for integrating passwordless authentication, passkey support, and multi-factor authentication (MFA) into Android applications.

## Modules

This SDK is composed of several modules, each designed for specific use cases:

- **[LoginIDAuth](./auth/README.md)**: The main entry point for a standalone authentication solution. It includes everything you need for passkey creation, authentication, transaction confirmation, and management.

- **[LoginIDMFA](./mfa/README.md)**: A powerful library for orchestrating stateful multi-factor authentication flows. It supports passkeys, OTPs, and external authenticators.

- **[LoginIDCheckoutMFA](./mfa/docs/LoginIDCheckoutMFA.md)**: A specialized helper built on `LoginIDMFA` for secure, transaction-based authentication in e-commerce checkout scenarios.

- **[LoginIDCore](./core/README.md)**: An internal module containing shared code, utilities, and models used across the other SDK components. It is not intended for direct use.

## Requirements

- Android 9.0+ (API level 28+)

## Contact and Support

- **Email**: support@loginid.io
- **Documentation**: https://docs.loginid.io
- **Forum**: https://forum.loginid.dev
- **Bug Reports**: https://loginid.dev
- **Dashboard**: https://dashboard.loginid.io

## License

This project is licensed under the Apache 2.0 License. See [LICENSE.md](./LICENSE.md) for details.
