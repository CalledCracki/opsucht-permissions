# OPSucht PermissionsAPI

Eine modulare und generische **PermissionsAPI** für das OPSucht Netzwerk.  
Dieses Projekt stellt eine einheitliche Schnittstelle zu verschiedenen Permission-Systemen bereit  
(z. B. **LuckPerms**, **GroupManager**, **PermissionsEx**) und ermöglicht es,  
Netzwerk-Plugins unabhängig vom verwendeten Permission-System zu entwickeln.

---

## 🧬 Projektstruktur

| Modul | Beschreibung |
|--------|---------------|
| **permissionapi** | Kern-API mit `PermissionProvider`-Interface |
| **permissionbukkit** | Implementierung für Bukkit/Paper-Server |
| **permissionbungee** | Implementierung für BungeeCord-/Waterfall-Proxies |

---
<br>

## ⚙️ Installation (für Entwickler)

### Maven
```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/CalledCracki/opsucht-permissions</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>net.opsucht</groupId>
    <artifactId>permissionapi</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

### Gradle (Kotlin DSL)
```kotlin
repositories {
    maven { url = uri("https://maven.pkg.github.com/CalledCracki/opsucht-permissions") }
}

dependencies {
    implementation("net.opsucht:permissionapi:1.0.0-SNAPSHOT")
}
```

---
<br>

## 🚀 Beispielverwendung

```java
import net.opsucht.permission.api.Permission;

UUID playerId = player.getUniqueId();

if (Permission.get().has(playerId, "opsucht.fly")) {
    player.sendMessage("§aDu darfst fliegen!");
} else {
    player.sendMessage("§cKeine Berechtigung!");
}
```

Oder um eine Permission zu vergeben:
```java
Permission.get().add(playerId, "opsucht.fly");
```

---
<br>

## 🧠 Unterstützte Permission-Systeme

| System | Modul | Status |
|---------|--------|--------|
| **LuckPerms** | Bukkit + Bungee | ✅ Vollständig |
| **GroupManager** | Bukkit | ⚙️ Teilweise |
| **PermissionsEx** | Bukkit | ⚙️ In Arbeit |

---
<br>

## 🛠️ Build & Deploy

Das Projekt verwendet **Maven** mit einem Multi-Module-Setup.  
Der `permissionapi`-Build wird automatisch über GitHub Actions nach  
**GitHub Packages** deployed.

```bash
mvn clean package
```

---
<br>

## 👥 Lizenz & Credits

**Autor:** [@CalledCracki](https://github.com/CalledCracki)  
**Lizenz:** MIT  
**Version:** 1.0.0-SNAPSHOT

---

> OPSucht Permissions — „Eine API, ein Interface, alle Systeme.“

