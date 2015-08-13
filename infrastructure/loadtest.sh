#!/bin/sh

MESSAGE=`cat <<EOF
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean convallis sagittis magna non placerat. Praesent quis aliquet nulla, et sodales lectus. Sed vulputate urna vitae massa pellentesque, eget placerat lorem laoreet. Integer malesuada velit pharetra tellus iaculis, eget iaculis mi iaculis. Ut dapibus, ante vitae laoreet dictum, enim eros tristique ipsum, sed blandit dolor leo et dolor. Duis bibendum, nunc ac iaculis hendrerit, lorem metus pretium sem, at blandit felis erat ac lectus. In hac posuere.
EOF`

docker run eigrad/tcpkali --first-message 'device-1' --message "$MESSAGE" --message-rate 1000 --connections 10 "$1" &
docker run eigrad/tcpkali --first-message 'device-2' --message "$MESSAGE" --message-rate 1000 --connections 10 "$1" &
docker run eigrad/tcpkali --first-message 'device-3' --message "$MESSAGE" --message-rate 1000 --connections 10 "$1"
