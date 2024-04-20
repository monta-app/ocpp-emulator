output_path=icon.iconset

# the convert command comes from imagemagick
for size in 16 32 64 128 256 512; do
  half="$(($size / 2))"
  convert icon.png -resize x$size $output_path/icon_${size}x${size}.png
  convert icon.png -resize x$size $output_path/icon_${half}x${half}@2x.png
done

iconutil -c icns $output_path
