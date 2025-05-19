# Finans Yönetim Sistemi

Bu proje, kişisel finans yönetimi için geliştirilmiş bir Java uygulamasıdır. Hem MySQL hem de MongoDB veritabanlarını destekler ve hem grafiksel kullanıcı arayüzü (Swing) hem de konsol arayüzü sunar.

## Özellikler

- Gelir ve gider takibi
- Kategori bazlı bütçe yönetimi
- Tasarruf hedefleri oluşturma ve takip
- Notlar ve hatırlatıcılar
- MySQL ve MongoDB desteği
- Swing GUI ve konsol arayüzü

## Gereksinimler

- Java 11 veya üzeri
- Maven
- MySQL veya MongoDB veritabanı

## Kurulum

1. Projeyi klonlayın:
```bash
git clone [proje-url]
```

2. Maven bağımlılıklarını yükleyin:
```bash
mvn clean install
```

3. Veritabanı kurulumu:

### MySQL için:
```sql
-- Veritabanını oluşturun
CREATE DATABASE finance_manager;

-- Tabloları oluşturun
[SQL dosyasındaki tüm CREATE TABLE komutlarını çalıştırın]
```

### MongoDB için:
MongoDB'de otomatik olarak gerekli koleksiyonlar oluşturulacaktır.

## Kullanım

Uygulamayı başlatmak için:

```bash
mvn exec:java -Dexec.mainClass="com.financeapp.Main"
```

Uygulama başladığında:
1. Arayüz seçimi yapın (GUI veya Konsol)
2. Veritabanı seçimi yapın (MySQL veya MongoDB)
3. Veritabanı bağlantı bilgilerini girin

## Veritabanı Bağlantı Bilgileri

### MySQL için:
- Host: localhost
- Port: 3306
- Database: finance_manager
- Username: [kullanıcı adınız]
- Password: [şifreniz]

### MongoDB için:
- Connection String: mongodb://localhost:27017

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. 