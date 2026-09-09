# HTTP Proxy Connect — Android Native Java

โปรเจกต์ Android Studio แบบ **Native Java** สำหรับจัดการโปรไฟล์ HTTP Proxy และทดสอบการเชื่อมต่อผ่าน Proxy โดยไม่ต้องพึ่ง Expo หรือ React Native

## เปิดโปรเจกต์

1. แตกไฟล์ ZIP
2. เปิด Android Studio แล้วเลือก **Open** ไปที่โฟลเดอร์ `http-proxy-android-java`
3. รอ Gradle Sync และติดตั้ง Android SDK Platform 35 / Build Tools ตามที่ Android Studio แจ้ง
4. สร้าง Emulator หรือเชื่อมต่ออุปกรณ์ Android ที่เปิด USB debugging
5. กด **Run** เพื่อทดสอบ

## Build บน GitHub Actions

โปรเจกต์มี workflow ที่ `.github/workflows/android.yml` แล้ว โดยใช้ชื่อ **Android Cloud Build Pipeline** เมื่อ push ไปยัง branch `main` หรือ `master` หรือกด **Run workflow** เอง GitHub Actions จะติดตั้ง JDK 17, ใช้ Gradle 8.13, build Debug APK และสร้าง GitHub Release พร้อมแนบ APK ให้อัตโนมัติ

### ให้ APK ไปอยู่ใน Releases

Workflow จะสร้าง GitHub Release ใหม่ทุกครั้งที่มีการ push ไปยัง `main` หรือ `master` โดยใช้ timestamp เป็นชื่อ tag เช่น `build-20260909-192600` ไม่ต้องสร้าง tag เอง:

```bash
git add .
git commit -m "Build Android APK"
git push origin main
```

จากนั้นเปิดแท็บ **Actions** รอ workflow ทำงานจนสำเร็จ แล้วเปิดแท็บ **Releases** จะพบ Release ชื่อ `Build 20260909-192600` และไฟล์ APK ในส่วน Assets โดย Release ล่าสุดจะถูกตั้งเป็น Latest อัตโนมัติ

คำสั่ง build ในเครื่องมีดังนี้:

```bash
chmod +x gradlew
./gradlew assembleDebug
```

ไฟล์ APK จะอยู่ที่ `app/build/outputs/apk/debug/app-debug.apk`

### วิธีสร้าง repository และ push ครั้งแรก

สร้าง repository ว่างบน GitHub ก่อน จากนั้นรันคำสั่งต่อไปนี้ที่โฟลเดอร์โปรเจกต์ โดยแทนที่ `USERNAME` และ `REPOSITORY` ด้วยข้อมูลของคุณ:

```bash
git init
git add .
git commit -m "Initial Android Java XML app"
git branch -M main
git remote add origin https://github.com/USERNAME/REPOSITORY.git
git push -u origin main
```

ไม่ควร commit `local.properties`, keystore, password หรือ token ลง GitHub ไฟล์เหล่านี้ถูกกันไว้ใน `.gitignore` แล้ว

> Workflow นี้สร้าง **Debug APK** สำหรับทดสอบเท่านั้น หากจะเผยแพร่ Google Play ควรสร้าง Release AAB และใช้ keystore ที่เก็บผ่าน GitHub Secrets อย่างปลอดภัย การ publish Release ใช้สิทธิ์ `contents: write` เพื่อให้ GitHub Actions สร้าง Release ได้

## ฟีเจอร์ที่มีในโครงนี้

- หน้าหลักแบบ dark UI ใกล้เคียง HTTP Custom: toolbar, Main/Log tabs, traffic card และ bottom navigation
- Layout หลักอยู่ที่ `app/src/main/res/layout/activity_main.xml` และ logic interaction อยู่ที่ `MainActivity.java`
- ฟอร์มชื่อโปรไฟล์, Host/IP, Port, Username และ Password
- เก็บข้อมูลโปรไฟล์ด้วย `EncryptedSharedPreferences`
- ทดสอบ HTTP Proxy จริงด้วย `HttpURLConnection` และ `java.net.Proxy`
- รองรับการขยายเป็นหลายโปรไฟล์, VpnService, background service และการตั้งค่าแอปเพิ่มเติม
- ใช้ Java 17, Android Gradle Plugin 8.7.3, compileSdk/targetSdk 35 และ minSdk 24

## ข้อจำกัดสำคัญ

โค้ดชุดนี้เป็น **HTTP Proxy client/tester** ระดับแอป ไม่ใช่ VPN ระดับระบบ Android การตั้งค่า `java.net.Proxy` มีผลเฉพาะ request ที่แอปสร้างเองเท่านั้น หากต้องการ route ทราฟฟิกทั้งเครื่อง ให้พัฒนาต่อด้วย `android.net.VpnService` และ native packet forwarding หรือใช้ VPN protocol ที่ออกแบบมาสำหรับงานนี้ เช่น WireGuard/OpenVPN โปรดตรวจสอบข้อกำหนด Google Play และกฎหมายท้องถิ่นก่อนเผยแพร่

## จุดที่ควรพัฒนาต่อ

- เปลี่ยนปุ่ม placeholder ของ SSH, V2Ray, Psiphon, OpenVPN และ UDP Custom เป็นโมดูลจริงตาม requirement
- เพิ่ม Room database สำหรับหลายโปรไฟล์และการจัดหมวดหมู่
- เพิ่ม `VpnService` หากต้องการระบบ VPN จริง
- เพิ่ม certificate pinning, logging ที่ไม่เก็บข้อมูลลับ และ privacy policy
