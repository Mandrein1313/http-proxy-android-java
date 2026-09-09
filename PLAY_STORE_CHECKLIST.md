# Google Play preparation checklist

ก่อนสร้าง release ให้เปลี่ยน `applicationId` และชื่อ package เป็นของผู้พัฒนา, เพิ่มไอคอนแอปทุก density, ตั้ง versionCode/versionName, สร้าง keystore สำหรับ signing และเก็บ keystore นอก repository

ตรวจสอบ target SDK ตามข้อกำหนดปัจจุบันของ Google Play, สร้าง Android App Bundle (`.aab`) แบบ release, ทดสอบบนอุปกรณ์จริงหลายรุ่น และเปิด Play App Signing ตามความเหมาะสม

เตรียม privacy policy ที่อธิบายการเก็บ Host, Username และ Password โดยชัดเจน แม้โปรเจกต์นี้เก็บข้อมูลภายในเครื่องก็ตาม กรอก Data safety form ให้ตรงกับพฤติกรรมจริง และหลีกเลี่ยงการขอ permission ที่ไม่จำเป็น

หากเพิ่ม `VpnService` ต้องอธิบายฟังก์ชัน VPN ใน store listing, ใช้ permission และ disclosure ที่เหมาะสม, ไม่เก็บหรือส่งทราฟฟิกเกินวัตถุประสงค์ที่แจ้ง และทดสอบ policy ของ Google Play ก่อนส่ง review
