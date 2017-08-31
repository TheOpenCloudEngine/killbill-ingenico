## 플러그인 스펙 작성

```
vi /var/tmp/bundles/plugins/plugin_identifiers.json

.
.
  "killbill-ingenico": {
    "plugin_name": "killbill-ingenico",
    "language": "java"
  }
}
```

## 플러그인 복사하기

```
mkdir /var/tmp/bundles/plugins/java/killbill-ingenico
mkdir /var/tmp/bundles/plugins/java/killbill-ingenico/1.0

cp <path of jar file> /var/tmp/bundles/plugins/java/killbill-ingenico/1.0/
```

## 플러그인 링크 걸기

```
cd /var/tmp/bundles/plugins/java/killbill-ingenico

ln -s /var/tmp/bundles/plugins/java/killbill-ingenico/1.0 SET_DEFAULT
```
