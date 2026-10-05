# Laapor — Aplikasi Laporan Progress Pekerjaan Konstruksi Real-Time

Aplikasi Android native berbasis **Kotlin** dan **Jetpack Compose (Material 3)** untuk dokumentasi lapangan proyek konstruksi. Foto dokumentasi hanya dapat diambil langsung via kamera handphone secara *real-time* dengan watermark otomatis, manajemen kontraktor & PIC, menu catatan lapangan, gallery per tanggal, dan ekspor laporan PDF format **A4 Landscape** dengan aturan 4 space gambar per lokasi dan ringkasan progress di samping kiri layout.

---

## 📁 Struktur Folder Project

```text
Laapor/
├── .github/
│   └── workflows/
│       └── build-apk.yml               # Workflow GitHub Actions untuk build APK otomatis
├── app/
│   ├── build.gradle.kts                # Konfigurasi modul app, dependencies, dan SDK
│   ├── proguard-rules.pro              # Konfigurasi ProGuard / R8
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml     # Manifest aplikasi, izin kamera, dan FileProvider
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt     # Aktivitas utama & navigasi
│       │   │   ├── data/
│       │   │   │   ├── local/          # Room Database (AppDatabase, DAOs)
│       │   │   │   ├── model/          # Data model (Project, ProgressPhoto, ContractorInfo, dll.)
│       │   │   │   └── repository/     # Repository layer
│       │   │   ├── engine/
│       │   │   │   ├── ImageWatermarkHelper.kt  # Pemroses watermark otomatis real-time
│       │   │   │   ├── PhotoLayoutEngine.kt     # Engine 4 space gambar per lokasi
│       │   │   │   └── ReportPdfGenerator.kt    # Pembuat PDF A4 Landscape native
│       │   │   └── ui/
│       │   │       ├── components/     # Komponen pastel M3 & dialog
│       │   │       ├── screens/        # Layar aplikasi (Home, Project, Camera, Gallery, Report, Preview, Profile)
│       │   │       ├── theme/          # Tema warna pastel & tipografi
│       │   │       └── viewmodel/      # LapoorViewModel
│       │   └── res/
│       │       ├── drawable/           # Ikon launcher & logo badge Laapor
│       │       ├── values/             # strings.xml, colors.xml
│       │       └── xml/                # file_paths.xml (FileProvider)
│       └── test/java/com/example/
│           └── ExampleRobolectricTest.kt  # Unit test logika aplikasi
├── gradle/
│   ├── libs.versions.toml              # Version Catalog dependencies
│   └── wrapper/
│       ├── gradle-wrapper.jar          # Binary Gradle wrapper
│       └── gradle-wrapper.properties   # Konfigurasi versi Gradle (9.3.1)
├── .gitignore                          # Filter file lokal dan keystore
├── build.gradle.kts                    # Root build script
├── gradle.properties                   # Parameter memori & AndroidX Gradle
├── gradlew                             # Script Gradle wrapper untuk Linux/macOS
├── gradlew.bat                         # Script Gradle wrapper untuk Windows
├── metadata.json                       # Metadata AI Studio
└── README.md                           # Panduan lengkap proyek
```

---

## 🚀 Cara Memindahkan Proyek ke GitHub

### Langkah 1: Export dari Google AI Studio
1. Pada antarmuka AI Studio di pojok kanan atas, klik tombol **Settings** (ikon titik tiga atau roda gigi).
2. Pilih **"Export as ZIP"** (atau jika tersedia tombol **Push to GitHub**, Anda bisa langsung menggunakannya).
3. Ekstrak file ZIP di komputer Anda.

### Langkah 2: Inisialisasi Git & Push ke GitHub
Buka terminal / Command Prompt pada folder hasil ekstrak, lalu jalankan perintah berikut:

```bash
# Inisialisasi Git lokal
git init

# Tambahkan semua file proyek
git add .

# Buat commit pertama
git commit -m "Initial commit - Proyek Laapor Android"

# Ubah nama branch utama menjadi main
git branch -M main

# Hubungkan dengan repository GitHub Anda
# (Ganti URL di bawah dengan URL repository GitHub Anda)
git remote add origin https://github.com/USERNAME/laapor-android.git

# Push seluruh project ke branch main
git push -u origin main
```

---

## ⚙️ Cara Menjalankan Build APK Otomatis di GitHub Actions

Setiap kali Anda melakukan `git push` ke branch `main`, GitHub Actions akan otomatis:
1. Memeriksa (*checkout*) kode sumber.
2. Memasang JDK 17 (Temurin).
3. Menyiapkan keystore signing yang aman.
4. Menjalankan `./gradlew assembleDebug assembleRelease`.
5. Mengunggah file APK ke tab **Artifacts**.

### Menjalankan Build secara Manual (Opsional)
1. Buka repository Anda di browser: `https://github.com/USERNAME/laapor-android`.
2. Klik tab **Actions** di bagian atas.
3. Pada panel sebelah kiri, klik workflow **"Build Android APK (Laapor)"**.
4. Klik tombol **Run workflow** > pilih branch `main` > klik **Run workflow**.

---

## 📥 Lokasi Hasil APK & Cara Download

1. Di halaman repository GitHub Anda, klik tab **Actions**.
2. Klik proses build terbaru yang berstatus tanda centang hijau (**✓ Build Release & Debug APK**).
3. Gulir ke bagian paling bawah ke bagian **Artifacts**.
4. Klik pada nama artifact: **`laapor-apk`**.
5. Browser akan mengunduh file `laapor-apk.zip` yang di dalamnya berisi:
   - `app-release.apk`
   - `app-debug.apk`

---

## 📱 Cara Menginstal APK ke Handphone Android (Tanpa Kabel USB)

### Cara 1: Mengunduh langsung lewat Browser HP
1. Buka browser (Google Chrome) di handphone Android Anda.
2. Buka halaman GitHub repository > tab **Actions** > buka build terbaru > unduh artifact **`laapor-apk`**.
3. Ekstrak zip dan buka file `app-release.apk` (atau `app-debug.apk`).
4. Jika muncul peringatan keamanan *"Izinkan instal dari sumber ini"*, centang **Izinkan / Allow**.
5. Tekan tombol **Install**. Aplikasi **Laapor** siap digunakan!

### Cara 2: Kirim via WhatsApp / Telegram / Google Drive
1. Unduh file APK di laptop/PC.
2. Kirimkan file `.apk` ke akun WhatsApp / Telegram pribadi Anda, atau unggah ke Google Drive.
3. Buka pesan tersebut dari handphone Android Anda, ketuk file APK, dan pilih **Install**.

---

## 🔒 Konfigurasi Keystore Produksi (Opsional untuk Google Play Store)

Jika Anda ingin menandatangani APK release menggunakan keystore produksi milik Anda sendiri:
1. Buka repository GitHub > tab **Settings** > **Secrets and variables** > **Actions**.
2. Tambahkan Secrets berikut:
   - `STORE_PASSWORD`: Kata sandi keystore Anda
   - `KEY_PASSWORD`: Kata sandi alias key Anda
3. Workflow akan otomatis menggunakan rahasia tersebut tanpa pernah menampilkan kata sandi ke dalam source code.
