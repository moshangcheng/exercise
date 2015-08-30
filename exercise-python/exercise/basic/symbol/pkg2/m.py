__author__ = 'moshangcheng'

print("importing " + __name__)


def say():
    print("this is exercise.basic.symbol.pkg2.m.say()")


if __name__ == '__main__':
    from exercise.basic.symbol.pkg1 import m

    m.say()

from ..pkg1 import m

m.say()
