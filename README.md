literumilo
==========

This library provides a GUI for adding spell-checking to JTextComponents with a pluggable spelling library.

---

In order to use the library, you must first initialize the `Spellchecker` to be used by the library.  As an example, for `HunspellSpellchecker` one would do something like the following:

```java
Hunspell dict = ... // retrieve HunspellBridJ dictionary.

HunspellSpellchecker spellchecker = new HunspellSpellchecker();
spellchecker.addDictionary(new Locale("en", "US"), /* or whatever locale you wish */
                            dict);

Literumilo.setSpellchecker(spellchecker);
```

After that is done, you can now register components to be spellchecked.  If you have no context menus registered on the text component already, it is as simple as doing the following:

```java
Literumilo.register(component);
```

However, if you do already have context menus, there is a way to work with that.  First thing you will need to do is to register the component:

```java
SpellcheckedComponent spellcheckedComponent = Literumilo.register(component, false);
```

Note how you pass `false` as a second parameter, which instructs it to not register the menus, and how you keep a handle on the `SpellcheckedComponent` returned from the register method.

Now that you have it registered, you can retrieve a `List<Action>` that will perform the replacement of the words, as well as retrieve a `List<JMenuItem>` that will allow the user to switch languages.

```java
JMenu spellingMenu = ... // retrieve the menu you are adding spellchecking suggestions to
/* where e is the MouseEvent that caused the menu creation...the Point is used to figure out which word was triggered on */
List<Action> spellcheckSuggestions = spellcheckedComponent.getSpellcheckSuggestions(e.getPoint());
for (Action suggestion : spellcheckSuggestions) {
	spellingMenu.add(suggestion);
}

// Add the language selection items.
JMenu languageMenu = ... // retrieve the menu that you are adding the language selectors to
for (JMenuItem language : spellcheckedComponent.getLanguageSelectors()) {
	languageMenu.add(language);
}
```

---

Known Limitations:

* The provided `Tokenizer` is quite primitive.

---

Note that this project is unlikely to undergo any more major development.  I am no longer working on the project that I created this for, and none of my current projects involve Swing.  However, I will happily accept pull requests, and I will try to be responsive to any issues found with the library.