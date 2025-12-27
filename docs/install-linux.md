# Installing on Linux Environments

## Ubuntu using Gnome Desktop
The `.desktop` entry for the application has to be copied manually.

1. Install the package using  `sudo dpkg -i pomotimo_<version>_<architecture>.deb`
2. Navigate to `/opt/pomotimo/lib`. You will see two .desktop entries.
3. Copy `pomotimo.desktop` to the correct folder: 
```bash
    sudo cp /opt/pomotimo/lib/pomotimo.desktop /usr/share/applications/
 ```
You can ignore the other .desktop file.
