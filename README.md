# Jumia Services Exercise Solution

## About:
This is the solution to Jumia's Recruitment Process Exercise

## Project Structure:
the project is split into three directories:
1. Backend
which contains the source code for the `Spring Boot` based REST Web Service backend, this also contains the `customers.db` SQLite Database
2. Frontend
which contains the source code for the frontend, which I opted to use Android for it, as well as the release APK
3. Requirements
which contains the requested requirements from Jumia

## How To Use:
### Backend:
1. clone the project to your computer
2. goto the `Backend/phone_numbers` directory and open a command window in this directory
3. use the provided `dockerfile` to build a docker image using the following command:
```shell
docker build --tag=phone_numbers:latest .
```
4. run the docker image using the following command:
```shell
docker run -p8080:8080 phone_numbers:latest
```
its important to map the ports as shown above, as the Frontend is hardcoded to port `8080`

### Frontend:
1. download the APK from [here](https://raw.githubusercontent.com/abdalmoniem/JumiaServicesTest/master/FrontEnd/JumiasTaskPhoneNumberViewer/APK/phoneNumbersClient.apk)
2. use any android emulator (Android Studio or Genymotion if you don't want to install a whole IDE) or just sideload it to an actual android phone, but keep in mind that the emulator or the physical phone need to be on the same network as the backend server
3. launch the app, the first thing you're going to see is a prompt to enter the server's IP address, you can find this by typing the following command:
a. on linux/unix systems:
```shell
ifconfig
```
b. on windows systems:
```shell
ipconfig
```