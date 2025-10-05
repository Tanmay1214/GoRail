# GoRail – Your Journey Our Priority

GoRail is a modern Android application designed to make train travel easier, faster, and more reliable.  
It allows users to **search trains, check seat availability, view fares and track live running status** — all in one place.

---

## ✨ Features

###  🔐 1. Secure OTP Login and Firebase Authentication
GoRail ensures a safe and seamless sign-in experience using **Firebase Authentication** 
Users can register and log in with their **mobile number via OTP verification** or just by signing in with google, eliminating the need for creating any passwords  
Each user’s details (name, phone, email) are securely stored in **Firebase Realtime Database**, ensuring security.
<img width="300" height="350" src="https://github.com/user-attachments/assets/50148967-7fe8-400a-bf35-8ceeb338c0f4"/> <img width="300" height="350" src="https://github.com/user-attachments/assets/49ee831e-c8b2-41c0-8c67-68fbc89dafe8"/>




---

### 2.👤 Profile Management(in Developement)& Passenger List

Each user will have a personalized profile section where details like name, email, passenger list, and mobile number will be shown.
GoRail also allows users to save passenger lists directly to Firebase, which are automatically loaded whenever the user opens the booking screen.
Passengers can be added, edited, or deleted anytime — making future bookings faster and effortless.

<img width="350" height="246" alt="Screenshot 2025-10-05 171037" src="https://github.com/user-attachments/assets/dedc6bac-9b96-462f-8060-f416ec1905dd" />

<img width="300" height="350" src="https://github.com/user-attachments/assets/2901807b-999a-46e1-8cd4-2db9cdcbd917"/>
<img width="300" height="350" src="https://github.com/user-attachments/assets/b5ae002d-edfe-41ea-bb6f-cae3db648158"/>

---
### 🚉 3. Train Search with Smart Filters
Users can search for trains between any two stations by entering the **source, destination, and travel date**.  
The app fetches live train data using our APIs and displays key information like:
- Train Name & Number
- Departure & Arrival Time
- Total Journey Duration
- Availability of seats and fares
- Running Days (SMTWTFS format)

This allows travelers to easily compare trains and pick the best option.

<img width="300" height="350" src="https://github.com/user-attachments/assets/925b712f-8400-47e3-9352-16b22cdacd0a"/> <img width="300" height="350" src="https://github.com/user-attachments/assets/c343f704-7d8a-4473-bfc7-f0353f330f2e"/>




---

### 💺 4. Real-Time Seat Availability and Fare
Once trains are listed, users can instantly check **seat availability and fare details** for every class (1A, 2A, 3A, 3E, SL, etc.).  
Data is fetched dynamically from **our API**, showing accurate results in real time.  
Each class is displayed in a **horizontal scrollable card view** with color-coded availability indicators.

<img width="300" height="350" src="https://github.com/user-attachments/assets/c343f704-7d8a-4473-bfc7-f0353f330f2e"/> <img width="300" height="350" src="https://github.com/user-attachments/assets/6cb08fd9-a8d5-426f-a2e9-72e701ccfe52"/>



---

### 📅 5. Smart Date Selector
The app includes a **horizontal date selection bar** that displays the current and upcoming 5–6 days.
- The default selected date is always the **current day**.
- Seat availsblity overall status is also shown for each date simultaneoulsy
- When a new date is selected, train data updates automatically.  
  This design gives users a fast and intuitive way to compare availability across multiple days.

<img width="300" height="350" src="https://github.com/user-attachments/assets/30596921-6471-4444-854f-9312a5475b54"/>


---

### 🚀 6. Live Train Running Status(Backend is complete Frontend is in developement right now)
Using Confirmtkt’s live data thru my API, users can track a train’s **real-time running position**.  
The feature shows:
- Current location of the train
- Departed, Current, and Upcoming stations
- Actual Time and Delayed time

- 
  <img width="400" height="500" alt="Screenshot 2025-10-05 145440" src="https://github.com/user-attachments/assets/a625c729-6d3a-40a2-8860-d125cd1020d5" />


---

### 🧭 7. Dashboard(in developement)& Modern UI
GoRail features a **clean and interactive dashboard** that displays quick insights like:
- Recent train searches
- Shortcuts for different features
- Upcoming journeys  
  It follows **Material Design principles**, providing a modern, fast, and visually appealing user experience.

<img width="400" height="500" alt="Screenshot 2025-10-05 145440" src="https://github.com/user-attachments/assets/cff04e19-0374-4219-b7e0-7c1d07d41dbd" /><img width="400" height="500" alt="Screenshot 2025-10-05 145440" src="https://github.com/user-attachments/assets/60fd330c-d010-4359-91bd-5d1ba7890015" />


---

### ⚡ 8. Optimized Backend with Flask
The backend uses **Python Flask APIs** deployed on **Render**, handling seat availability,fetching trains and running status requests efficiently
Firebase is used for authentication purposes
Data scraping logic is optimized to minimize memory usage and ensure faster responses — even under heavy loads.

---

## 🛠️ Tech Stack
- **Language:** Java
- **Database:** Firebase Realtime Database
- **Authentication:** Firebase Phone OTP-based Login and Google Sign In
- **Backend:** Flask (Python) APIs deployed on Render
- **APIs Used(ALL APIs used are self made):**
    - Seat Availability & Fare Scraper(SELF MADE)
    - Running Status Tracker(SELF MADE)
    - Fetch Trains B/W 2 Stations(SELF MADE)
    - Route Fetcher(SELF MADE)
  

---


---

## Deployment
Backend APIs are hosted on **Render**, providing dynamic seat availability, train status, and train fares.

---

## 💡 Future Enhancements
- Add PNR status tracking
- Profile Management System
- Train Running Status Feature Frontend

---

## 🧑‍💻 Developed By
**Tanmay Shrivastava**
**25UEC245**
> Student First Year, ECE @ LNMIIT Jaipur  
> Passionate Learning Android Developer | Python Enthusiast | Learning Web Developer

---

