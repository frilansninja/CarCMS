const CustomerTable = ({ customers, onEdit, onDelete, onShowUsers }) => (
    <table className="border w-full">
        <thead>
        <tr className="bg-gray-100">
            <th className="border p-2">Namn</th>
            <th className="border p-2">Org. Nummer</th>
            <th className="border p-2">Telefon</th>
            <th className="border p-2">E-post</th>
            <th className="border p-2">Adress</th>
            <th className="border p-2">Åtgärder</th>
        </tr>
        </thead>
        <tbody>
        {customers.map((customer) => (
            <tr key={customer.id} className="text-center">
                <td className="border p-2">{customer.name}</td>
                <td className="border p-2">{customer.orgNumber}</td>
                <td className="border p-2">{customer.phone}</td>
                <td className="border p-2">{customer.email}</td>
                <td className="border p-2">{customer.address}</td>
                <td className="border p-2">
                    <button className="bg-yellow-500 text-white px-3 py-1" onClick={() => onEdit(customer)}>
                        Redigera
                    </button>
                    <button className="bg-red-500 text-white px-3 py-1" onClick={() => onDelete(customer.id)}>
                        Ta bort
                    </button>
                    <button className="bg-blue-500 text-white px-3 py-1" onClick={() => onShowUsers(customer.id)}>
                        Visa användare
                    </button>
                </td>
            </tr>
        ))}
        </tbody>
    </table>
);

export default CustomerTable;
