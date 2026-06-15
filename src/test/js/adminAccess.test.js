describe('Admin Access', () => {

    test('guarda privilegios de admin', () => {

        Storage.prototype.setItem = jest.fn();

        sessionStorage.setItem('isAdmin', 'true');
        sessionStorage.setItem('currentUser', 'Valentina');

        expect(sessionStorage.setItem)
            .toHaveBeenCalledWith(
                'isAdmin',
                'true'
            );

        expect(sessionStorage.setItem)
            .toHaveBeenCalledWith(
                'currentUser',
                'Valentina'
            );
    });
});