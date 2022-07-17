LDIRS="lecture01 lecture02"

for LDIR in $LDIRS
do
  echo
  echo "--------------------------------------------------------------------------------"
  echo "Processing  $LDIR"
  echo "--------------------------------------------------------------------------------"
  echo
  pushd $LDIR/src/main/asciidoc || exit
  po2txt -t kstreams.adoc kstreams-en.po kstreams-en.adoc
  popd || exit
done
