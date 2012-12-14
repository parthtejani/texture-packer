Texture Packer
===========
This is a simple program to pack bitmaps into a texture atlas.

Usage
---------
### Input Arguments

1. A source directory that contains subdirectories which contain the actual images
2. A destination directory to store the texture atlases and texture coordinates data.

### Commands
Command Line

    git clone https://github.com/parthtejani/texture-packer.git
    cd texture-packer
    mkdir bin
    javac -d bin src/com/parthtejani/texturepacker/*.java
    cd bin
    java com.parthtejani.texturepacker.TexturePacker /some/input/dir/bitmaps  /some/output/dir/atlases 
In Code

    TexturePacker packer = new TexturePacker("/some/input/dir/bitmaps", "/some/output/dir/atlases");
    packer.run();
Screen Shots
------------------
The input directory is "bitmaps". It contains subdirectories "arial\_22", "arial\_24", etc that contain the actual images. The name of the generated texture atlas is equal to the subdirectory name.

![screenshot](https://raw.github.com/parthtejani/texture-packer/master/screenshots/input.png "Input")

The output directory is "atlases", and contains the texture atlases, and a text file describing the coordinates of where the bitmap is located in the atlas.

![screenshot](https://raw.github.com/parthtejani/texture-packer/master/screenshots/output.png "Output")

An example texture atlas.

![screenshot](https://raw.github.com/parthtejani/texture-packer/master/screenshots/texture-atlas.png "Input")

An example text file. The first line contains 3 tokens: the number of bitmaps in the texture atlas and the width/height of the texture atlas. All other lines have 5 tokens: bitmap name, left, right, bottom, and top positions.

![screenshot](https://raw.github.com/parthtejani/texture-packer/master/screenshots/texture-data.png "Input")

Notes
---------
* This program uses PNG as the image format for the texture atlases.
* I use this so I can draw fonts and textures with OpenGL in a game.