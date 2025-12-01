DisasterNet 🚨
Offline Emergency Communication App

DisasterNet is an offline, peer-to-peer communication tool designed for emergency situations where cellular networks and internet connectivity are unavailable. Built for Android, it uses Bluetooth Low Energy (BLE) to create a local broadcast network that allows users to send public messages, discover nearby survivors, and broadcast critical SOS alerts with GPS coordinates.

📱 Features
Public Shoutbox 📢

• Broadcast short text messages instantly to all nearby users
• Custom byte-level chunking ensures reliable BLE communication
• Clear visual separation between sent and received messages

SOS Emergency System 🆘

• Press and hold the SOS button for 3 seconds to trigger an emergency alert
• Automatically fetches device GPS coordinates
• High-visibility red/yellow SOS messages for urgent recognition
• Button color changes confirm transmission

Nearby Device Discovery 📡

• Continuously scans for DisasterNet users
• Shows device name and Bluetooth address
• UI ready for private chat (logic under development)

🛠️ Tech Stack

• Language: Kotlin
• Minimum Android Version: Android 8.0 (API 26)
• Architecture: MVVM
• Connectivity: BLE Advertising and Scanning
• Location Services: Google Play Services (FusedLocationProvider)
• UI Components: Fragments, Navigation Component, RecyclerView, ConstraintLayout

⚙️ How It Works (Networking Core)

DisasterNet overcomes BLE’s 31-byte advertisement packet limitation using a custom fragmentation and reassembly system:

Messages are divided into small 15-byte chunks

Each chunk contains a 2-byte header with Message ID and chunk numbers

Broadcasting uses a “Shout → Pause → Repeat” pattern to avoid BLE hardware overload

Each device generates a unique UUID to prevent duplication and self-messages

🚀 Getting Started
Prerequisites

• Two Android devices (Android 8.0 or higher)
• Bluetooth and Location enabled

Installation

Clone the repository:

git clone https://github.com/yourusername/DisasterNet.git

Then open the project in Android Studio, sync Gradle, connect your device, and run the app.

Required Permissions

• BLUETOOTH_SCAN
• BLUETOOTH_ADVERTISE
• ACCESS_FINE_LOCATION

📸 Screenshots

Shoutbox | SOS Alert | Nearby Users
(Add screenshots here when available)

🚧 Future Improvements

• Mesh networking for extended range
• Wi-Fi Direct file sharing
• Background scanning service
• End-to-end private chat system

🤝 Contributing

Contributions are welcome.
Please feel free to submit improvements, issues, or pull requests.

📄 License

DisasterNet is licensed under the MIT License.
See the LICENSE file for details
