import com.sun.source.tree.Tree;

public class TestAvlTree {
    public static void main(String[] args) {
        MyAVLTree<String> tree = new MyAVLTree<>();
        long startTime = System.nanoTime();
        for(int i=0; i<500; i++) {
            tree.insert(""+(int)(Math.random()*500));
        }
        tree.printAll();
        long finishTime = System.nanoTime();
        System.out.println((finishTime-startTime)/1000000000.0);
    }

}

class MyAVLTree <T extends Comparable<T>> {
    private MyAVLTreeNode<T> root;

    public void printAll(){
        printAll(root);
    }

    public void printAll(MyAVLTreeNode<T> root) {
        if(root!=null) {
            printAll(root.getLeftChild());
            System.out.println(root.getItem());
            printAll(root.getRightChild());
        }
    }

    public T search(T item) {
        return search(root,item);
    }

    public T search(MyAVLTreeNode<T> root, T item){
        if(root==null) {
            return null;
        } else if(item.compareTo(root.getItem())<0) {
            return search(root.getLeftChild(),item);
        } else if(item.compareTo(root.getItem())>0) {
            return search(root.getRightChild(),item);
        } else {
            return root.getItem();
        }
    }

    public void delete(T item) {
        delete(root, item);
    }

    public void delete(MyAVLTreeNode<T> root, T item) {
        if(root==null) {
        } else if(item.compareTo(root.getItem())<0) {
            delete(root.getLeftChild(),item);
        } else if(item.compareTo(root.getItem())>0) {
            delete(root.getRightChild(),item);
        } else {
            if(root.getLeftChild()==null) {
                if(root.getRightChild()==null) {
                    //leaf node
                    if(root.getIsLeftChild()) {
                        root.getParent().setLeftChild(null);
                        root.setParent(null);
                    } else {
                        if(root.getParent()==null) {
                            root = null;
                        } else {
                            root.getParent().setRightChild(null);
                            root.setParent(null);
                        }
                    }
                } else {
                    if(root.getIsLeftChild()) {
                        root.getParent().setLeftChild(root.getRightChild());
                        root.getRightChild().setParent(root.getParent());
                        root.setParent(null);
                        root.setRightChild(null);

                    } else {
                        if(root.getParent()==null) {
                            root = root.getRightChild();
                            root.setParent(null);
                        } else {
                            root.getParent().setRightChild(root.getRightChild());
                            root.getRightChild().setParent(root.getParent());
                            root.setParent(null);
                            root.setRightChild(null);
                        }
                    }
                }
            } else {
                if(root.getRightChild()==null) {
                    if(root.getIsLeftChild()) {
                        root.getParent().setLeftChild(root.getLeftChild());
                        root.getLeftChild().setParent(root.getParent());
                        root.setParent(null);
                        root.setLeftChild(null);
                    } else {
                        if(root.getParent()==null) {
                            root = root.getLeftChild();
                            root.setParent(null);
                        } else {
                            root.getParent().setRightChild(root.getLeftChild());
                            root.getLeftChild().setParent(root.getParent());
                            root.setParent(null);
                            root.setLeftChild(null);
                        }
                    }
                } else {
                    MyAVLTreeNode<T> tmp = getSmallest(root.getRightChild());
                    T tmpKey = tmp.getItem();
                    root.setItem(tmpKey);
                    tmp.getParent().setLeftChild(null);
                    tmp.setParent(null);
                }
            }
        }
    }

    public MyAVLTreeNode<T> getSmallest(MyAVLTreeNode<T> root) {
        MyAVLTreeNode<T> node = root;
        while(node.getLeftChild()!=null) {
            node = node.getLeftChild();
        }
        return node;
    }

    public void insert(T item) {
        if(root==null) {
            root = new MyAVLTreeNode<>();
            root.setItem(item);
        } else {
            insert(root, item);
        }
    }

    public void insert(MyAVLTreeNode<T> root, T item) {
        if(item.compareTo(root.getItem())<0) {
            if(root.getLeftChild()==null) {
                MyAVLTreeNode<T> node = new MyAVLTreeNode<>();
                node.setItem(item);
                root.setLeftChild(node);
                root.setLeftHeight(1);
                node.setParent(root);
                node.setIsLeftChild(true);
                rebalance(root);
            } else {
                root.setLeftHeight(root.getLeftHeight()+1);
                insert(root.getLeftChild(),item);
            }
        } else if(item.compareTo(root.getItem())>0) {
            if(root.getRightChild()==null) {
                MyAVLTreeNode<T> node = new MyAVLTreeNode<>();
                node.setItem(item);
                root.setRightChild(node);
                root.setRightHeight(1);
                node.setParent(root);
                node.setIsLeftChild(false);
                rebalance(root);
            } else {
                root.setRightHeight(root.getRightHeight()+1);
                insert(root.getRightChild(),item);
            }
        } else {
            while(root.getParent()!=null) {
                if(root.getIsLeftChild()) {
                    root.getParent().setLeftHeight(root.getParent().getLeftHeight()-1);
                } else {
                    root.getParent().setRightHeight(root.getParent().getRightHeight()-1);
                }
                root = root.getParent();
            }
        }
    }

    public void rebalance(MyAVLTreeNode<T> root) {
        boolean needRecall = false;
        if(getDiff(root)>1) {
            if(getDiff(root.getLeftChild())>=0) {
                if(getDiff(root.getLeftChild())==0) needRecall = true;
                LLRotation(root);
            } else {
                LRRotation(root);
            }
            root = root.getParent();
        } else if(getDiff(root)<-1){
            if(getDiff(root.getRightChild())<=0) {
                if(getDiff(root.getRightChild())==0) needRecall = true;
                RRRotation(root);
            } else {
                RLRotation(root);
            }
            root = root.getParent();
        }

        if(root.getParent()!=null && needRecall) rebalance(root.getParent());
        else if(root.getParent()!=null && !needRecall) {
            while(root.getParent()!=null) {
                if(root.getIsLeftChild()) {
                    root.getParent().setLeftHeight(root.getParent().getLeftHeight()-1);
                } else {
                    root.getParent().setRightHeight(root.getParent().getRightHeight()-1);
                }
                root = root.getParent();
            }
        }
    }

    public int getDiff(MyAVLTreeNode<T> root) {
        if(root==null) return 0;
        return root.getLeftHeight()-root.getRightHeight();
    }

    public void LLRotation(MyAVLTreeNode<T> root) {
        // LL Rotation
        MyAVLTreeNode<T> leftChild = root.getLeftChild();
        MyAVLTreeNode<T> grandParent = root.getParent();
        boolean rootIsLeft = root.getIsLeftChild();
        // root 와 leftChild disconnect
        leftChild.setParent(null);
        root.setLeftChild(null);
        root.setLeftHeight(0);
        root.setIsLeftChild(false);
        if(leftChild.getRightHeight()!=0) {
            //root 와 leftChild의 rightChild connect
            root.setLeftChild(leftChild.getRightChild());
            root.setLeftHeight(leftChild.getRightHeight());
            leftChild.getRightChild().setParent(root);
        }
        leftChild.setRightChild(root);
        root.setParent(leftChild);
        leftChild.setRightHeight(1+Math.max(root.getRightHeight(),root.getLeftHeight()));
        if(rootIsLeft) {
            grandParent.setLeftChild(leftChild);
            grandParent.setLeftHeight(1+Math.max(leftChild.getLeftHeight(),leftChild.getRightHeight()));
            leftChild.setParent(grandParent);
            leftChild.setIsLeftChild(true);
        } else {
            if(grandParent!=null) {
                grandParent.setRightChild(leftChild);
                grandParent.setRightHeight(1+Math.max(leftChild.getLeftHeight(),leftChild.getRightHeight()));
                leftChild.setParent(grandParent);
            }
            leftChild.setIsLeftChild(false);
        }
    }


    public void RRRotation(MyAVLTreeNode<T> root) {
        // LL Rotation
        MyAVLTreeNode<T> rightChild = root.getRightChild();
        MyAVLTreeNode<T> grandParent = root.getParent();
        boolean rootIsLeft = root.getIsLeftChild();
        // root 와 rightChild disconnect
        rightChild.setParent(null);
        root.setRightChild(null);
        root.setRightHeight(0);
        root.setIsLeftChild(true);
        if(rightChild.getLeftHeight()!=0) {
            //root 와 rightChild의 leftChild connect
            root.setRightChild(rightChild.getLeftChild());
            root.setRightHeight(rightChild.getLeftHeight());
            rightChild.getLeftChild().setParent(root);
        }
        rightChild.setLeftChild(root);
        root.setParent(rightChild);
        rightChild.setLeftHeight(1+Math.max(root.getRightHeight(),root.getLeftHeight()));
        if(rootIsLeft) {
            grandParent.setLeftChild(rightChild);
            grandParent.setLeftHeight(1+Math.max(rightChild.getLeftHeight(),rightChild.getRightHeight()));
            rightChild.setParent(grandParent);
            rightChild.setIsLeftChild(true);
        } else {
            if(grandParent!=null) {
                grandParent.setRightChild(rightChild);
                grandParent.setRightHeight(1+Math.max(rightChild.getLeftHeight(),rightChild.getRightHeight()));
                rightChild.setParent(grandParent);
            }
            rightChild.setIsLeftChild(false);
        }
    }

    public void LRRotation(MyAVLTreeNode<T> root) {
        RRRotation(root.getLeftChild());
        LLRotation(root);
    }

    public void RLRotation(MyAVLTreeNode<T> root) {
        LLRotation(root.getRightChild());
        RRRotation(root);
    }

}

class MyAVLTreeNode <T extends Comparable<T>> {
    private T item;
    private MyAVLTreeNode<T> parent;
    private boolean isLeftChild = false;

    public boolean getIsLeftChild() {
        return isLeftChild;
    }

    public void setIsLeftChild(boolean leftChild) {
        isLeftChild = leftChild;
    }

    public MyAVLTreeNode<T> getParent() {
        return parent;
    }

    public void setParent(MyAVLTreeNode<T> parent) {
        this.parent = parent;
    }

    private MyAVLTreeNode<T> leftChild;
    private MyAVLTreeNode<T> rightChild;
    private int leftHeight;
    private int rightHeight;

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public MyAVLTreeNode<T> getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(MyAVLTreeNode<T> leftChild) {
        this.leftChild = leftChild;
    }

    public MyAVLTreeNode<T> getRightChild() {
        return rightChild;
    }

    public void setRightChild(MyAVLTreeNode<T> rightChild) {
        this.rightChild = rightChild;
    }

    public int getLeftHeight() {
        return leftHeight;
    }

    public void setLeftHeight(int leftHeight) {
        this.leftHeight = leftHeight;
    }

    public int getRightHeight() {
        return rightHeight;
    }

    public void setRightHeight(int rightHeight) {
        this.rightHeight = rightHeight;
    }
}