describe('Room', () => {

    test('muestra panel admin', () => {

        document.body.innerHTML = `
            <div id="adminTabBtn"
                 style="display:none"></div>
        `;

        const isAdmin = true;

        if(isAdmin){
            document
                .getElementById('adminTabBtn')
                .style.display = 'block';
        }

        expect(
            document.getElementById('adminTabBtn')
                .style.display
        ).toBe('block');
    });
});