# Generative ISA Genetic Algorithm using Poker Data

This project seeks to understand and explore the options for
generating an Instruction Set Architecure (ISA) using a
Genetic Algorithm.

The ISA is defined by a series of opcodes, with a full instruction
set being generated using a Genetic Algorithm approach. The opcodes
provide primative machine language operations which can potentially
solve the problem of evaluating poker hands.

The poker hand dataset is used as a means to evaluating the fitness
of the genetic algorithm. The fitness of an organism can be measured
by how many poker hands the organism can correctly identify from the
data set.

In order to do this, the simulation create an initial population using
random data. The population is evaluated for fitness against a set of
previously scored poker hands. If the organism gets the evaluation
correct, it adds to its fitness score. The most fit organisms are
selected for breeding into the next generation.

The OpCodes can be seen in the opcodes.md file, which provides the
basic operations which are performed by the instruction. Common instructions
are provided, including PUSH, ADD, COMPARE, etc. The operations also
include some non-standard codes, which are specific to the problem
domain, such as an operation to read the rank of any given card in
the players hand, etc.
