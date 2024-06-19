<p align="center">
  <img src="./docs/icons/icon.png" alt="Logo" width="256">
</p>

<img src="https://img.shields.io/github/v/release/monta-app/ocpp-emulator"/>

# OCPP Charge Point Emulator

This project's goal is to allow users to emulate all of the features of OCPP (both 1.6 & 2.0.1) in order to allow
easier testing and speed up local development, here is an overview of what has been implemented in the project so
far

### OCPP 1.6

- Core (Done)
- Firmware Management (Done)
- Local Auth List Management (Done)
- Reservation (Not Done)
- Smart Charging (Semi Done)
- Remote Trigger (Done)

### OCPP 2.0.1

- Currently under development, the OCPP 2.0.1 version is not yet fully implemented, but we're working on it.

### How to run

If you're using Intellij IDEA, you can just run one of the two configurations that are saved in the `.run` folder

- `Run V16` for OCPP 1.6
- `Run V201` for OCPP 2.0.1

If you're just using the terminal, you can run the following command:

**OCPP 1.6**

```shell
./gradlew run v16:run
```

**OCPP 2.0.1**

```shell
./gradlew run v201:run
```

## What's up with the 🤓?

Clicking the icon gives access to "message interception". The primary purpose is to have a high degree of control over which messages
are sent and received by the charge point. That way it is possible to replicate potentially buggy behavior or custom implementations in
a one-off manner without needing to change the actual programming of the charge point. For "normal operation" of the charge point the
standard interface should be sufficient.

Also note that the message interception functions are not hooked up to the internal machinery of the charge point. For example, sending
a `StopTransaction` message will not actually change the state of an ongoing charge to be stopped. That means using these functions
also makes it very easy to put the charge point into a state that does not match up with what the CSMS is expecting, which can quickly
lead to unexpected behavior.

## Executables

If you only care about running the application you can find the latest release on
the [releases page](https://github.com/monta-app/ocpp-emulator/releases) we are currently building executables for
Windows, Linux and MacOS.

## How to contribute

We welcome contributions from everyone who is willing to improve this project. Whether you're fixing bugs, adding new
features, improving documentation, or suggesting new ideas, your help is greatly appreciated! Just make sure you
follow these simple guidelines before opening up a PR:

- Follow the [Code of Conduct](CODE_OF_CONDUCT.md): Always adhere to the Code of Conduct and be respectful of others
  in the community.
- Test Your Changes: Ensure your code is tested and as bug-free as possible.
- Update Documentation: If you're adding new features or making changes that require it, update the documentation
  accordingly.



