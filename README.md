# CSDNExport
Export CSDN article to Wordpress. Support article content, category, tags , comments and article image download.


## config

Change the config.template.json file to config.json

``` json
Pullsoft ImageProtect
{
  "wordpress": {
    "DBIP": "127.0.0.1",
    "DBPort": 3306,
    "DBName": "wordpress",
    "DBUser": "wordpress",
    "DBPassword": "wordpress",
    "UserID": 1,
    "ImgUrl": "/wp-content/uploads/csdn/"
  },
  "csdn": {
    "userName": "csdn",
    "pageCount": 1,
    "onlyOriginal": true
  }
}
```

| Key  | Desc |
| ------  | ------ |
| UserID | The ID in table wp_users  |
| ImgUrl | The wordpress upload image url. Will replace the csdn image url|
|pageCount | The csdn article list page count|
|onlyOriginal | Only export the original article|


## Import

The CSDN articles import to wordpress, the post status is draft. The CSDN article's images can only access from csdn host.
So we download the images to csdn folder. You can copy the folder under the wordpress's upload folder. []( []( []( []() ) ) )



