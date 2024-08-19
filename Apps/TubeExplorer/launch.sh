#!/bin/sh

# Set the working directory
cd "$(dirname "$0")"

# Check if TubeExplorer2 exists
if [ -f TubeExplorer2 ]; then
    # Delete TubeExplorer if it exists
    if [ -f TubeExplorer ]; then
        rm -f TubeExplorer
        echo "TubeExplorer deleted."
    fi

    # Rename TubeExplorer2 to TubeExplorer
    mv TubeExplorer2 TubeExplorer
    echo "TubeExplorer2 renamed to TubeExplorer."
else
    echo "TubeExplorer2 does not exist."
fi

# Copy LibSDL files only if they don't exist
if [ ! -f libSDL2.so ]; then
    cp /usr/trimui/lib/libSDL2-2.0.so.0 .
    mv libSDL2-2.0.so.0 libSDL2.so
    echo "libSDL2 copied."
fi

if [ ! -f libSDL2_ttf.so ]; then
    cp /usr/trimui/lib/libSDL2_ttf-2.0.so.0 .
    mv libSDL2_ttf-2.0.so.0 libSDL2_ttf.so
    echo "libSDL2_ttf copied."
fi

if [ ! -f libSDL2_image.so ]; then
    cp /usr/trimui/lib/libSDL2_image-2.0.so.0 .
    mv libSDL2_image-2.0.so.0 libSDL2_image.so
    echo "libSDL2_image copied."
fi

export LD_LIBRARY_PATH="$(dirname "$0"):/lib64:/usr/trimui/lib:/usr/lib:/usr/trimui/lib:$LD_LIBRARY_PATH"
export CLR_OPENSSL_VERSION_OVERRIDE=1.1

mkdir -p /etc/ssl/certs/
cp -vf "$(dirname "$0")/ca-certificates.crt" /etc/ssl/certs/
./TubeExplorer &> errors.txt

exit 0
