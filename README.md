# 🚆 GoRail – Smarter Train Travel Made Simple

GoRail is a modern Android application designed to make train travel easier, faster, and more reliable.  
It allows users to **search trains, check seat availability, view fares, track live running status, and manage bookings** — all in one place.

---

## ✨ Features

### 🔐 1. Secure OTP Login and Firebase Authentication
GoRail ensures a safe and seamless sign-in experience using **Firebase Authentication**.  
Users can register and log in with their **mobile number via OTP verification**, eliminating the need for passwords.  
Each user’s details (name, phone, email) are securely stored in **Firebase Realtime Database**, ensuring both security and simplicity.

---

### 👤 2. Profile Management & Passenger List
Each user has a personalized **profile section** where details like name, email, and mobile number are shown.  
GoRail also allows users to **save passenger lists** directly to Firebase, which are automatically loaded whenever the user opens the booking screen.  
Passengers can be **added, edited, or deleted** anytime — making future bookings faster and effortless.

---

### 🚉 3. Train Search with Smart Filters
Users can search for trains between any two stations by entering the **source, destination, and travel date**.  
The app fetches live train data using APIs and displays key information like:
- Train Name & Number
- Departure & Arrival Time
- Total Journey Duration
- Running Days (SMTWTFS format)

This allows travelers to easily compare trains and pick the best option.

---

### 💺 4. Real-Time Seat Availability and Fare
Once trains are listed, users can instantly check **seat availability and fare details** for every class (1A, 2A, 3A, 3E, SL, etc.).  
Data is fetched dynamically from **Ixigo / Erail APIs** or via the **Flask scraper backend**, showing accurate results in real time.  
Each class is displayed in a **horizontal scrollable card view** with color-coded availability indicators.

---

### 📅 5. Smart Date Selector
The app includes a **horizontal date selection bar** that displays the current and upcoming 5–6 days.
- The default selected date is always the **current day**.
- When a new date is selected, train data updates automatically.  
  This design gives users a fast and intuitive way to compare availability across multiple days.

---

### 🚀 6. Live Train Running Status
Using Confirmtkt’s live data thru my API, users can track a train’s **real-time running position**.  
The feature shows:
- Current location of the train
- Departed, Current, and Upcoming stations
- Actual Time and Delayed time
- 

---

### 🧭 7. Dashboard & Modern UI
GoRail features a **clean and interactive dashboard** that displays quick insights like:
- Recent train searches
- Travel shortcuts
- Upcoming journeys  
  It follows **Material Design principles**, providing a modern, fast, and visually appealing user experience.

---

### ⚡ 8. Optimized Backend with Flask
The backend uses **Python Flask APIs** deployed on **Railway** and **Render**, handling seat availability and running status requests efficiently.  
Data scraping logic is optimized to minimize memory usage and ensure faster responses — even under heavy loads.

---

## 🛠️ Tech Stack
- **Language:** Java
- **Database:** Firebase Realtime Database
- **Authentication:** Firebase OTP-based Login
- **Backend:** Flask (Python) APIs deployed on Render
- **APIs Used(ALL APIs used are self made):**
    - Seat Availability & Fare Scraper(SELF MADE)
    - Running Status Tracker(SELF MADE)
    - Fetch Trains B/W 2 Stations(SELF MADE)
    - Route Fetcher(SELF MADE)
  

---


---

## 🚀 Deployment
Backend APIs are hosted on **Railway** and **Render**, providing dynamic seat availability, train status, and IRCTC integration support.

---

## 📸 Screenshots *(optional)*
> Add screenshots here for UI preview  
Example:  
`![Home Screen](screenshots/home.png)`  
`![Train Search](screenshots/train_search.png)`

---

## 💡 Future Enhancements
- Add PNR status tracking
- Push notifications for delays or cancellations
- Add Train Running Status Feature UI
- 

---

## 🧑‍💻 Developed By
**Tanmay Shrivastava**
> Student First Year, ECE @ LNMIIT Jaipur  
> Passionate Android Developer | Python Enthusiast | Railway Tech Innovator

---

