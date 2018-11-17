# Extract source file from SDLXLIFF

SDL's bilingual format .sdlxliff includes an "internal-file" element at the start of the document that contains a base64 encoded string of the original source file imported into the project.

For exmaple:

```xml
<?xml version="1.0" encoding="utf-8"?>
<xliff xmlns:sdl="http://sdl.com/FileTypes/SdlXliff/1.0" xmlns="urn:oasis:names:tc:xliff:document:1.2" version="1.2" sdl:version="1.0">
  <file original="__PATH_TO_SOURCE_FILE__" datatype="x-sdlfilterframework2" source-language="en-US">
    <header>
      <reference>
        <internal-file form="base64">UEsDBBQAAAAIAHd......mIzZm0ubDRpLnhtbFBLBQYAAAAAAQABAD4AAAA8AgAAAAA=</internal-file>
      </reference>
....
```

Usually, the original source file/s will be included in the SDL package. If this isn't the case, this base64 string could be used to generate the original source file for reference.

The encoded string is actually a zipped version of the source file (you'll see the PK file signature if you try and write out the purely decoded string), so it's necessary to read as a zip input stream and write out the file. 
