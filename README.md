# PermissionsAPI

Eine modulare und generische **PermissionsAPI**.  
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

### Kompatibilität

| Component | Supported Versions |
|-----------|-------------------|
| **Minecraft (Bukkit/Paper)** | 1.18.x - 1.21.x |
| **Minecraft (Bungee/Waterfall)** | Latest |
| **Java** | 17+ |
| **LuckPerms** | 5.4+ |
| **GroupManager** | 3.2+ |
| **PermissionsEx** | 1.22.3+ |

---
<br>

## 💾 Maven Installation

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/CalledCracki/generic-permissions</url>
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
    maven { url = uri("https://maven.pkg.github.com/CalledCracki/generic-permissions") }
}

dependencies {
    implementation("net.opsucht:permissionapi:1.0.0-SNAPSHOT")
}
```

---
<br>

## 🚀 Beispielverwendung

### Synchronous API

```java
import net.opsucht.permission.api.Permission;

UUID playerId = player.getUniqueId();

if (Permission.get().has(playerId, "f2p.fly")) {
    player.sendMessage("§aDu darfst fliegen!");
} else {
    player.sendMessage("§cKeine Berechtigung!");
}
```

### Permission hinzufügen/entfernen

```java
// Synchron (blockierend)
Permission.get().add(playerId, "opsucht.fly");
Permission.get().remove(playerId, "opsucht.build");

// Asynchron (nicht-blockierend, empfohlen)
Permission.get().addAsync(playerId, "opsucht.fly")
    .thenRun(() -> player.sendMessage("§aPermission hinzugefügt!"))
    .exceptionally(throwable -> {
        player.sendMessage("§cFehler: " + throwable.getMessage());
        return null;
    });
```

### Mit Caching (optional)

```java
import net.opsucht.permission.common.cache.CachedPermissionProvider;
import net.opsucht.permission.api.Permission;

// Wrap provider mit Caching für bessere Performance
PermissionProvider baseProvider = ProviderManager.detectProvider();
PermissionProvider cachedProvider = new CachedPermissionProvider(baseProvider);
Permission.set(cachedProvider);
```

---
<br>

## 🧠 Unterstützte Permission-Systeme

| System | Modul | Status | Features |
|---------|--------|--------|----------|
| **LuckPerms** | Bukkit + Bungee | ✅ Vollständig | Async Support |
| **GroupManager** | Bukkit | ✅ Vollständig | World-based |
| **PermissionsEx** | Bukkit | ⚙️ In Arbeit | Multi-Version |
| **Native Bukkit** | Bukkit | ✅ Fallback | Basic Only |

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

> Permissions+ — „Eine API, ein Interface, alle Systeme.“

