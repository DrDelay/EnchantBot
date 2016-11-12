# EnchantBot - AceOnline lab bruteforce-enchanting

[![Software License][ico-license]](LICENSE.md)
[![Latest Stable Version][ico-githubversion]][link-releases]

This program can use the laboratory in AceOnline like games and enchant your items.
It is especially useful on private servers, where enchant- & protect-cards are free, but reaching certain stages (e16) has a low chance and requires a huge amount of tries.

## Features

* Auto-enchant weapons and armor
* GUI with Status-logging
* Save/Load configs
* Specify exactly what (or whether) cards (protect, enchant-plus, stat) to use at each stage
* Automatically build the next weapon once one if finished, useful if you need many for Legend-upgrade
* Ressources completely customizeable by you

## Build

``` bash
$ mkdir build
$ javac -d ./build src/de/drdelay/enchantbot/tool/*.java src/de/drdelay/enchantbot/gui/*.java
$ cd build
$ jar cfe EnchantBot.jar de.drdelay.enchantbot.gui.EnchantSuite de/drdelay/enchantbot/*
```

## Usage

* Copy or link the enchant-numbers and *enchant.png* to *./res*
* Copy or link the identifier item you are using
* Set up your [ingame inventory](#inventory)
* Items must be at least **enchant 1**!
``` bash
$ java -jar EnchantBot.jar
```

## Inventory

* Put the identifier item at the top-left corner (Pos 1)
* Put the items to enchant at the end of your inventory

## Ressources

Enchants and positions are scanned by comparing pixelwise against the provided image files. Even the slightest difference in color matters.
This means, on certain servers you may have to create your own files. Black (#000000) pixels are ignored, so fill up anything but the text with.

## Credits

- [All Contributors][link-contributors]

## License

The MIT License (MIT). Please see the [License File](LICENSE.md) for more information.

[ico-license]: https://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat-square
[ico-githubversion]: https://poser.pugx.org/DrDelay/EnchantBot/v/stable

[link-releases]: https://github.com/DrDelay/EnchantBot/releases
[link-contributors]: ../../contributors
