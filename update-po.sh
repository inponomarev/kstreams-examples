LDIRS="lecture01 lecture02"

for LDIR in $LDIRS
do
  echo
  echo "--------------------------------------------------------------------------------"
  echo "Processing  $LDIR"
  echo "--------------------------------------------------------------------------------"
  echo
  pushd $LDIR/src/main/asciidoc || exit
  txt2po -P kstreams.adoc kstreams.pot
  msgmerge -U kstreams-en.po kstreams.pot
  popd || exit
done
