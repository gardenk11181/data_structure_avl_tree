import com.sun.source.tree.Tree;

public class TestAvlTree {
    public static void main(String[] args) {
        MyAVLTree<String> tree = new MyAVLTree<>();
        tree.insert("hello");
        tree.insert("hi");
        tree.insert("di");
        tree.insert("hehe");
        tree.delete("hehe");
        tree.delete("hi");
        System.out.println(tree.search("hi"));
    }

}

class MyAVLTree <T extends Comparable<T>> {
    private MyAVLTreeNode<T> root;


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
                node.setParent(root);
                node.setIsLeftChild(true);
//                root.setLeftHeight(1);
                rebalance(root);
            } else {
                insert(root.getLeftChild(),item);
            }
        } else if(item.compareTo(root.getItem())>0) {
            if(root.getRightChild()==null) {
                MyAVLTreeNode<T> node = new MyAVLTreeNode<>();
                node.setItem(item);
                root.setRightChild(node);
                node.setParent(root);
                node.setIsLeftChild(false);
//                root.setRightHeight(1);
                rebalance(root);
            } else {
                insert(root.getRightChild(),item);
            }
        }
    }

    public void rebalance(MyAVLTreeNode<T> root) {
        if(getDiff(root)>1) {
            if(getDiff(root.getLeftChild())>=0) {
                LLRotation(root);
            } else {
                LRRotation(root);
            }
        } else if(getDiff(root)<-1){
            if(getDiff(root.getRightChild())<=0) {
                RRRotation(root);
            } else {
                RLRotation(root);
            }
        }

        if(root.getParent()!=null) rebalance(root.getParent());
    }

    public int getDiff(MyAVLTreeNode<T> root) {
        if(root==null) return 0;
        return getHeight(root.getLeftChild())-getHeight(root.getRightChild());
    }

    public int getHeight(MyAVLTreeNode<T> root) {
        if(root==null) return 0;
        int left = getHeight(root.getLeftChild());
        int right = getHeight(root.getRightChild());
        return (Math.max(left,right)+1);
    }

    public void LLRotation(MyAVLTreeNode<T> root) {
        // LL Rotation
        MyAVLTreeNode<T> leftChild = root.getLeftChild();
        MyAVLTreeNode<T> grandParent = root.getParent();
        boolean rootIsLeft = root.getIsLeftChild();
        // root 와 leftChild disconnect
        leftChild.setParent(null);
        root.setLeftChild(null);
        if(leftChild.getRightHeight()!=0) {
            //root 와 leftChild의 rightChild connect
            root.setLeftChild(leftChild.getRightChild());
            leftChild.getRightChild().setParent(root);
        }
        leftChild.setRightChild(root);
        root.setParent(leftChild);
        if(rootIsLeft) {
            grandParent.setLeftChild(leftChild);
            leftChild.setParent(grandParent);
        } else {
            if(grandParent!=null) {
                grandParent.setRightChild(leftChild);
                leftChild.setParent(grandParent);
            }
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
        if(rightChild.getLeftHeight()!=0) {
            //root 와 rightChild의 leftChild connect
            root.setRightChild(rightChild.getLeftChild());
            rightChild.getLeftChild().setParent(root);
        }
        rightChild.setLeftChild(root);
        root.setParent(rightChild);
        if(rootIsLeft) {
            grandParent.setLeftChild(rightChild);
            rightChild.setParent(grandParent);
        } else {
            if(grandParent!=null) {
                grandParent.setRightChild(rightChild);
                rightChild.setParent(grandParent);
            }
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