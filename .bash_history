tree
ls
mkdir lab3
cd lab3
ls
mkdir CopyingFolder
tree
ls
cd CopyingFolder
touch copied.txt
tree
cd ..
tree
cd CopyingFolder
clear
tree
cd ..
tree
cd CopyingFolder
cp copied.txt ~/Documents/
cd ..
mkdir Documents
cp copied.txt ~/Documents/
cd CopyingFolder
cp copied.txt ~/Documents/
cd ..
tree
cd CopyingFolder
cp copied.txt /Documents
cp copied.txt ~/Documents/
cd ..
cp copied.txt/Documents
clear
tree
cd CopyingFolder
cp copied.txt ../Documents
tree
cd ../Documents
touch{one,two,tree,four,five}.txt
touch {one,two,tree,four,five}.txt
tree
cp [.txt] ../CopyingFolder
*.txt ../CopyingFolder
cp *.txt[!copied.txt] ../CopyingFolder
cp *.txt ../CopyingFolder
../CopyingFolder
cd ../CopyingFolder
tree
cd ..
mkdir Desktop
cd CopyingFolder
cp -r CopyingFolder ../Desktop
clear
cp -r ~CopyingFolder~Desktop
cp -r ~CopyingFolder ../Desktop
cp -r ~/CopyingFolder~/Desktop/
cp -r CopyingFolder/Desktop
tree
cd lab3/CopyingFolder
cp -r CopyingFolder Desktop/
cd ..
cp -r CopyingFolder Desktop/
cd Desktop
ls
tree
cd ../Documents
touch {one_document,two_document,three_document,four_document,five_document}.txt
ls
tree
echo 1 > one_document.txt
tree
echo 2 > two_document.txt
echo 3 > three_document.txt
echo 4 > four_document.txt
echo 5 > five_document.txt
cat *.txt
mv one_document.txt document_un.txt
mv two_document.txt document_deux.txt
mv three_document.txt document_trois.txt
mv four_document.txt document_quatre.txt
mv five_document.txt document_cinq.txt
tree
mv document_cinq.txt document_cinque.txt
tree
cat *.txt
mv document_un.txt document_one.txt
mv document_one[2~[2~[2~[2~[D.txt document_one.txt
mv document_one.txt document_un.txt
tree
clear
cd ..
mkdir MoveFolder
cd lab3
tree
Documents
cd Documents
cd lab3/Documents
tree
ls
cd lab3
tree
ls
cd lab3
tree
cd ~
ls
mkdir lab4
ls
cd lab4
ls -l > ~/contents.txt
pwd
scp -r lab4 gic-eng-mengheang@192.168.23.9:/home/gic-eng-mengheang
ls
cd lab4l
cd lab4
tree
scp -r lab4 gic-eng-mengheang@192.168.23.9:/home/gic-eng-mengheang
cd
ls
cd lab4
tree
cd
ls
clear
ls
git init
git remote add origin git@github.com:mengheangeng990/OperatingSystems.git
git add .vgit add .
git add .
git commit -m "Initial push of class activities and labs"
git branch -M main
git push -u origin main
git config --global user.name "Eng_Mengheang"
