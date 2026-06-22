# Stok Ikan Giling Mobile

Aplikasi Android native berbasis **Java** untuk tugas Pemrograman Berorientasi Objek.

**Application ID:** `com.bagas.stokikangiling`  
**Penyimpanan:** SQLite lokal di perangkat, tanpa akun dan tanpa koneksi internet.

## Fitur utama
- Dashboard ringkasan stok.
- Tambah produk ikan giling **beserta gambar wajib**.
- Edit dan hapus produk.
- Pencarian produk.
- Catat stok masuk.
- Catat stok keluar dengan validasi stok tidak boleh minus.
- Laporan ringkas, riwayat stok terbaru, export CSV untuk Excel, dan import catatan laporan.
- Penyimpanan lokal memakai SQLite.
- Sepuluh data contoh ikan Indonesia beserta foto yang tersedia offline.

## Fokus PBO
- `Produk` sebagai class induk.
- `IkanGiling extends Produk` sebagai inheritance.
- Encapsulation melalui atribut private dan getter/setter.
- Polymorphism melalui override `getRingkasan()`.
- Repository interface `ProdukRepository` sebagai abstraction.
- Service layer `StokService` untuk logika bisnis.

## Gambar produk
Gambar bawaan dibundel di APK. Gambar produk baru dipilih melalui pemilih dokumen Android dan izin bacanya disimpan secara persisten. Kredit serta lisensi foto ada di [ATTRIBUTION.md](ATTRIBUTION.md). Harga dan jumlah stok bawaan adalah data contoh, bukan harga pasar real-time.

## UI responsif
- Layout utama dibuat vertikal agar aman di ponsel kecil.
- Teks angka memakai auto-size agar tidak bertumpuk di layar sempit.
- Tombol utama dibuat lebar dan mudah disentuh.
- Kartu produk memakai susunan gambar + informasi + tombol aksi yang tetap rapi pada layar berbeda.

## Build lokal

Gunakan JDK 17 dan Android SDK, lalu jalankan:

```bash
./gradlew assembleDebug
```

Di Windows bisa memakai:

```powershell
.\gradlew.bat assembleDebug
```

APK terbentuk di `app/build/outputs/apk/debug/app-debug.apk`.

## Download APK dari GitHub Actions

Setiap push ke branch `main` menjalankan workflow **Build APK**. Untuk mengambil APK:

1. Buka tab **Actions** di repository GitHub.
2. Pilih workflow **Build APK** yang berhasil.
3. Download artifact **debug-apk**.

## Catatan integritas stok

Perubahan stok dan penulisan riwayat dijalankan dalam satu transaksi database. Stok produk yang sudah ada hanya dapat diubah dari menu stok masuk/keluar supaya jumlah dan histori tidak berbeda.
