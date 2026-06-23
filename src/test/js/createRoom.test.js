describe('Crear Sala', () => {

    test('guarda roomId y masterKey', () => {

        Storage.prototype.setItem = jest.fn();

        sessionStorage.setItem(
            'lastCreatedRoomId',
            'ROOM123'
        );

        sessionStorage.setItem(
            'lastCreatedMasterKey',
            'MK-123'
        );

        expect(sessionStorage.setItem)
            .toHaveBeenCalledWith(
                'lastCreatedRoomId',
                'ROOM123'
            );

        expect(sessionStorage.setItem)
            .toHaveBeenCalledWith(
                'lastCreatedMasterKey',
                'MK-123'
            );
    });
});