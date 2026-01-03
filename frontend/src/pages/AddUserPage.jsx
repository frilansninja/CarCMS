import {useState} from "react";
function AddUserPage() {
    const [customers, setCustomers] = useState([]);
    const [newUser, setNewUser] = useState({
        username: "",
        password: "",
        role: "USER",
        customerId: "",
    });

    //useEffect(() => {
    //    fetchCustomers();
    //}, []);

    const fetchCustomers = async () => {
        try {
            const response = await axios.get(`${API_URL}/companies`);
            setCustomers(response.data);
        } catch (error) {
            console.error("Kunde inte hämta kunder:", error);
        }
    };

    const handleAddUser = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem("accesstoken"); // Hämta sparad token
        //if (!newUser.customerId) return alert("Välj ett företag!");
        // Hårdkoda ett befintligt customerId för testning
        const companyId = 2; //newUser.customerId

        try {
            await axios.post(`${API_URL}/users/register/${companyId}`, {
                username: newUser.username,
                password: newUser.password,
                role: newUser.role,
            },
                {
                    headers: {
                        Authorization: `Bearer ${token}`, // Skicka token här
                    },
                }

            );
            setNewUser({ username: "", password: "", role: "USER", companyId: "" });
            alert("Användare tillagd!");
        } catch (error) {
            console.error("Kunde inte lägga till användaren:", error);
            alert("Fel vid skapande av användare.");
        }
    };

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Lägg till ny användare</h1>
            <form className="mb-4 flex flex-col gap-2" onSubmit={handleAddUser}>
                <input
                    className="border p-2"
                    type="text"
                    placeholder="Användarnamn"
                    value={newUser.username}
                    onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
                    required
                />
                <input
                    className="border p-2"
                    type="password"
                    placeholder="Lösenord"
                    value={newUser.password}
                    onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
                    required
                />
                <select
                    className="border p-2"
                    value={newUser.role}
                    onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
                >
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                </select>
                <select className="border p-2" disabled>
                    <option value="2">Företag ID 2</option>
                </select>

                <button type="submit" className="bg-green-500 text-white px-4 py-2">Lägg till</button>
            </form>
        </div>
    );
}

export default AddUserPage;
