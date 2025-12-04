# DisasterNet 🚨

**DisasterNet** is an offline, peer-to-peer communication tool designed for emergency situations where cellular networks and internet connectivity are unavailable. 
Built for Android, it utilizes **Bluetooth Low Energy (BLE)** to create a local broadcast network, allowing users to send public messages, discover nearby survivors, and broadcast
critical SOS alerts with GPS coordinates.

## 📱 Features

### 📢 Public Shoutbox
* **Broadcast Messages:** Send short text messages that are instantly received by all users within Bluetooth range.
* **Reliable Delivery:** Uses a custom byte-level chunking protocol to ensure messages are delivered reliably despite hardware limitations.
* **Visual Feedback:** Sent messages appear instantly on your screen; received messages are clearly distinguished.

### 🆘 SOS Emergency System
* **One-Touch Alert:** Press and hold the SOS button for 3 seconds to trigger an emergency broadcast.
* **GPS Integration:** Automatically fetches the device's high-accuracy GPS coordinates.
* **High Visibility:** SOS messages appear in red and yellow on receiving devices to demand immediate attention.
* **Visual Feedback:** The button changes color to indicate active status and confirmation of transmission.

### 📡 Nearby Device Discovery
* **Real-time Scanning:** Continuously scans for other DisasterNet users in the vicinity.
* **User List:** Displays a list of discovered devices with their names and Bluetooth addresses.
* **Private Chat Prep:** UI implementation for initiating private connections (logic currently in progress).

## 🛠️ Tech Stack

* **Language:** Kotlin
* **Minimum SDK:** API 26 (Android 8.0 Oreo)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Connectivity:** Bluetooth Low Energy (BLE) Advertising & Scanning
* **Location Services:** Google Play Services Location (FusedLocationProvider)
* **UI Components:** Fragments, Navigation Component, RecyclerView, ConstraintLayout

## ⚙️ How It Works (The Networking Core)

DisasterNet solves the limitations of standard BLE advertisement packets (~31 bytes) using a custom **Fragmentation and Reassembly Protocol**:

1.  **Message Chunking:** Large messages and SOS alerts are broken down into small, safe 15-byte payloads.
2.  **Custom Header Protocol:** Each packet includes a 2-byte header containing:
    * A unique **Message ID**.
    * **Chunk Index** and **Total Chunks** (bit-packed for efficiency).
3.  **Queue-Based Broadcasting:** To prevent hardware failure (`ADVERTISE_FAILED_INTERNAL_ERROR`), the app uses a **"Shout, Pause, Repeat"** cycle. It broadcasts chunks sequentially with a stable delay, ensuring hardware buffers are never overwhelmed.
4.  **Unique Identification:** Uses a generated UUID stored in `SharedPreferences` to uniquely identify users, preventing message duplication ("echoes") and self-reception.

## 🚀 Getting Started

### Prerequisites
* **Two** physical Android devices running Android 8.0 (Oreo) or higher.
* Bluetooth and Location must be enabled.

### Installation
1.  Clone the repository:
    ```bash
    git clone (https://github.com/yourusername/DisasterNet_App.git)
    ```
2.  Open the project in **Android Studio**.
3.  Sync Gradle files.
4.  Connect your physical device via USB.
5.  Run the app (`Shift + F10`).

### Permissions
The app requires the following permissions to function:
* `BLUETOOTH_SCAN` & `BLUETOOTH_ADVERTISE` (for networking)
* `ACCESS_FINE_LOCATION` (for SOS coordinates and BLE scanning requirements)

## 📸 Screenshots

| Shoutbox | SOS Alert | Nearby Users |
|:---:|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/55d39305-0736-42b2-8a30-d3ea0d636f00" width="250" /> | <img src="https://github.com/user-attachments/assets/ec5cb9c3-8e57-441f-a721-a63761ccb48c" width="250" /> | <img src="https://github.com/user-attachments/assets/7050f223-be8d-4ae6-b105-14e339f71a19" width="250" /> |


## 🚧 Future Improvements

* **Mesh Networking:** Implementing a relay system where devices re-broadcast received messages to extend range.
* **Private File Sharing:** Fully implementing the Wi-Fi Direct handover for sending photos and videos.
* **Background Service:** Ensuring scanning continues even when the screen is off for extended periods.

## 🤝 Contributing

Contributions are welcome! Please fork this repository and submit a pull request for any features, bug fixes, or enhancements.

## 📄 License


This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.





