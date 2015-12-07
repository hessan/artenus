#Changelog

##0.5.0
This is the first officially published version of the framework. The framework has evolved since
2012, and it is now time to properly keep track of its bug fixes, new features, and breaking
changes.

###Breaking Changes

####Filtered Entities
Filtered entities now return true for "equals" when their underlying entity is equal to the entity
they are compared to. So, for example, if you ask to remove an entity from a collection, it will be
removed either when it is directly in the collection, or it is at the end of a chain of filtered
entities that start with one that is in the collection.

####Effect Handling
Entity effect handling has been completely reworked. Before, the code for adding a shadow effect to
a sprite would look like this:
  
```
imageSprite.setEffect(new ShadowEffect(2, 3, 0.35f));
scene.add(imageSprite);
```

but now it now it looks like the following:

```
scene.add(new DropShadow(imageSprite).setOffset(2, 3).setShadowAlpha(0.35f));
```

or more concisely (which is less readable):

```
scene.add(new DropShadow(imageSprite, 2, 3, 0.35f));
```  

###Features

- The framework has been moved to OpenGL ES 2.0.
- Post-processing filters (blur, ghosting, etc) have been added to the framework.
- Effect handling has been revised and is now more robust and extensible.

### Bug Fixes

- A bug in slide input that caused large direction values to be returned
- Black-screen bug on resume
