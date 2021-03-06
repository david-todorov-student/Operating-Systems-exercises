Tribe Dinner 2.0 Problem 1 (0 / 0)

One tribe eats dinner together, from a big pot with limited number of meals. If there is food in the pot, every member will serve its self, and sits to eat. If the pot is empty, the chef is called to cook food to fill the whole pot, and everyone that hasn't eaten, waits for the cook to notify them that there is new food. The table has four seats, which means, maximum of four members can eat simultaniously. If there is no free space on the table, the members wait for someone to finish.

Your task is to synchronize the following scenario.

In the starter code, there are two classes defined: TribeMember and Chef. In the implementation, you should use the following methods from the already defined variable state:

    state.isPotEmpty()
        Returns boolean, symbolizes an empty pot.
        Called from all TribeMembers
        If more than one members checks, you will get an error message.
    state.fillPlate()
        Symbolizes taking food from the pot and filling your plate.
        Called from all tribe members.
        If the pot is empty, you will get an error.
        If multiple tribe members fill their plate, you will get an error.
    state.eat()
        Symbolizes eating on the table.
        Called from all tribe members.
        If more than four members eat simultaniously, you will get an error.
        If the method is called sequentially (one at a time), you will get an error.
    state.cook()
        Symbolizes cooking on behalf of the chef.
        Called only from the chef.
        If the method is called, and the pot has food, you will get an error.

За решавање на задачата, преземете го проектот со клик на копчето Starter file, отпакувајте го и отворете го со Eclipse или Netbeans.

Претходно назначените методи служат за проверка на точноста на сценариото и не смеат да бидат променети и мораат да бидат повикани.

Вашата задача е да го имплементирате методот execute() од класите TribeMember и Chef, кои се наоѓаат во датотеката SeptemberTribeDinnerSynchronization.java. При решавањето можете да користите семафори и монитори по ваша желба и нивната иницијализација треба да ја направите во init() методот.

При стартувањето на класата, сценариото ќе се повика 10 пати, со креирање на голем број инстанци од класата TribeMember и една инстанца од класата Chef. Кај сите TribeMember паралелно само еднаш ќе се повика нивниот execute() метод, додека пак Chef.execute() методот се повикува повеќе пати.
