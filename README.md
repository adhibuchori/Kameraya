# カメラ屋 (Kameraya)

## App Introduction

<div align="center">
  <img src="https://drive.google.com/uc?id=1PUX8hGEG_lhPjswZb0dI1rGRMPfRFPJb" alt="カメラ屋 (Kameraya) App Introduction">
  <p>Figure 1. カメラ屋 (Kameraya) App Introduction.</p>
</div>

カメラ屋 (Kameraya) is an innovative e-commerce platform dedicated exclusively to selling cameras. Whether you're a professional photographer or a hobbyist, Kameraya offers a wide range of high-quality cameras to capture your perfect moments. Discover the latest models and exclusive deals all in one place!  
Experience secure transactions with real-time payment status updates powered by Firebase Remote Config, ensuring peace of mind with every order. Enjoy the ultimate user experience, enhanced by smooth animations. Built with XML, Kameraya is designed for speed and reliability, offering a seamless shopping experience. Available starting from Android 8.0 "Oreo", Kameraya brings the world of cameras to your fingertips.

## App Preview
<div align="center">
  <img src="https://drive.google.com/uc?export=view&id=1kwhdjm2h6Gma_ri8pppNRNuroPPZFsjc" alt="カメラ屋 (Kameraya) App Preview">
  <p>Figure 2. カメラ屋 (Kameraya) App Preview.</p>
</div>

## Table of Contents
1. [App Introduction](#app-introduction)
2. [App Preview](#app-preview)
3. [Tech Stack](#tech-stack)
4. [App Features](#app-features)
5. [App Architecture](#app-architecture)
6. [Design Pattern](#design-pattern)
7. [Dependency Injection](#dependency-injection)
8. [Project Document](#project-document)
9. [Installation](#installation)

## Tech Stack
<div align="center">
 <img src="https://drive.google.com/uc?id=1CLCqrVSAyQaYYsBXScZOOogWUKvUqecy" alt="カメラ屋 (Kameraya) Tech Stack">
  <p>Figure 3. カメラ屋 (Kameraya) Tech Stack.</p>
</div>

## App Features
| **Feature**           | **Description**                                                                                                                                | **Technology**                                            |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| **Splash**            | Displays the app logo on a splash screen to create a professional first impression, with smooth animations for visual appeal.                  | XML                                                       |
| **On-Boarding**       | Provides onboarding screens that introduce the main features of the app, helping new users understand its value and usage.                     | XML, DataStore Preferences                                |
| **Authentication**    | Offers seamless authentication with auto-refresh tokens, eliminating the need for re-login as long as the refresh token is valid.              | XML, JWT Authentication, Retrofit, Data Store Preferences |
| **Product Catalog**   | Displays a product catalog with a list of available products, supporting Infinite Scrolling for loading more products as users scroll down.    | XML, Paging 3, Glide, Retrofit                            |
| **Search and Filter** | Enables easy search and filtering of products by category, price, sales, and reviews, enhancing product discoverability.                       | XML, Retrofit                                             |
| **Product Detail**    | Shows complete product information including description, price, reviews, and options to add to the wishlist or cart for convenient shopping.  | XML, Retrofit, Glide                                      |
| **Product Review**    | Allows users to view and write reviews about products they have purchased, sharing feedback with other users.                                  | XML, Retrofit                                             |
| **Share Products**    | Lets users share products with others via deep links, making it easy to share favorite products.                                               | XML, Deep Link                                            |
| **Shopping Cart**     | A feature that allows users to add products to a shopping cart and manage items they wish to purchase.                                         | XML, Room Database, Retrofit                              |
| **Wishlist**          | Lets users save products to a wishlist for later purchase, providing easy access to items of interest.                                         | XML, Room Database                                        |
| **Payment Gateway**   | Integrates with a payment gateway to securely and efficiently process transactions, ensuring smooth purchasing.                                | XML, Retrofit, Firebase Config                            |
| **Notification**      | Sends notifications about successful transactions, keeping users informed in real time.                                                        | XML, Retrofit                                             |
| **Transaction History**| Displays a history of all purchases made by the user, allowing them to track and manage their orders.                                         | XML, Retrofit                                             |
| **Localization**      | Supports multiple languages, making the app accessible to a global audience. Available for Android 13+.                                        | XML, Android Localization, Strings Resource               |
| **Dark Mode**         | Provides an option to switch between dark and light modes, enhancing user experience based on preference.                                      | XML, DataStore Preferences                                |
| **Infinite Scrolling**| Enables infinite scrolling for long product lists, allowing users to view more items without reloading the page.                               | Paging 3                                                  |
| **Firebase Crashlytics**| Monitors and reports app crashes in real time, helping developers quickly resolve issues and maintain app stability.                         | Firebase Crashlytics                                      |
| **Firebase Analytics**| Collects and analyzes user behavior data, providing valuable insights into app usage and user interactions.                                    | Firebase Analytics                                        |
| **API Logging**       | Monitors and logs API network activity for debugging and optimizing network requests, ensuring efficient data handling.                        | OkHttp Logging Interceptor                                |

## App Architecture
<div align="center">
 <img src="https://drive.google.com/uc?id=1ovtfICiUVKW645vAFOw3MfgSlBeyszdr" alt="カメラ屋 (Kameraya) App Architecture">
  <p>Figure 4. カメラ屋 (Kameraya) App Architecture.</p>
</div>

| **Module**    | **Description**                                                                                                     |
|---------------|---------------------------------------------------------------------------------------------------------------------|
| **UI Module** | Responsible for displaying the user interface, including screens, ViewModels, and handling the app's startup process. This module manages all user interactions and visual elements. |
| **Domain Module** | Contains domain models, repository interfaces, and use cases. It is independent of external libraries, ensuring stability and focusing solely on the app's business logic and core functionality. |
| **Data Module** | Manages network requests and local data operations. This module includes response and entity models, and repository implementations, handling data retrieval and persistence from remote servers or local databases. |

## Design Pattern
<div align="center">
 <img src="https://drive.google.com/uc?id=1ovtfICiUVKW645vAFOw3MfgSlBeyszdr" alt="カメラ屋 (Kameraya) App Design Pattern">
  <p>Figure 5. カメラ屋 (Kameraya) App Design Pattern</p>
</div>

| **Component** | **Description**                                                                                                           |
|---------------|---------------------------------------------------------------------------------------------------------------------------|
| **Model**     | Represents the application's data layer, including data models, data handling logic, and business rules. It interacts with data sources such as APIs or databases and serves as the authoritative source of data within the application. |
| **View**      | The user interface layer that presents data to the user and forwards user inputs to the ViewModel. |
| **ViewModel** | Acts as a mediator between the Model and the View. It processes data for display, manages the UI state, and handles user interactions. The ViewModel provides data to the View through observable data holders like LiveData or StateFlow, enabling the View to update in response to data changes without directly accessing the Model. |

## Dependency Injection
<div align="center">
 <img src="https://drive.google.com/uc?id=1iLHt4uzNePrDjxhIiVcIVYbcRTfIx5ju" alt="カメラ屋 (Kameraya) App Design Pattern">
  <p>Figure 6. カメラ屋 (Kameraya) Dependency Injection.</p>
</div>

## Project Document
Explore further insights and detailed information regarding this project by clicking on the following link: [カメラ屋 (Kameraya) Project Document](https://drive.google.com/file/d/1HKo9yEBHLmMOcmU_VikhMhKzXMmJClWS/view?usp=sharing).

## Installation
Download the application through the following Google Drive [link here](https://drive.google.com/file/d/1FmtERCCnyo1Edjp8KBAMWiNVEhHygxC-/view?usp=drive_link). Available starting from Android 8.0 "Oreo".
