import { useState } from 'react';
import { dailyEvents, weeklyRoles, mobs, items, serverRules, tutorials } from './data';
import { NavButton } from './components/ui/NavButton';
import { SectionTitle } from './components/ui/SectionTitle';
import { EventCard } from './components/EventCard';
import { RoleCard } from './components/RoleCard';
import { MobCard } from './components/MobCard';
import { ItemCard } from './components/ItemCard';
import { RouletteSimulator } from './components/RouletteSimulator';
import { ItemRollSimulator } from './components/ItemRollSimulator';
import { ActiveModifiers } from './components/ActiveModifiers';

// --- MAIN APP ---

function App() {
    const [view, setView] = useState('home'); 
    const [wikiSection, setWikiSection] = useState('events');
    const [simSection, setSimSection] = useState('roulette');
    const [searchTerm, setSearchTerm] = useState("");

    // Filter logic
    const filteredEvents = dailyEvents.filter(ev => 
        ev.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
        ev.type.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const renderWikiContent = () => {
        switch(wikiSection) {
            case 'events':
                return (
                    <div className="space-y-8">
                        <div className="bg-yellow-900/20 border border-yellow-700 p-4 flex flex-col md:flex-row gap-4 justify-between items-center">
                            <div>
                                <h3 className="text-yellow-500 text-2xl mb-1">⚠️ Efectos Acumulativos</h3>
                                <p className="text-lg text-gray-400">Total de eventos posibles: <span className="text-white font-bold">{dailyEvents.length}</span></p>
                            </div>
                            <input 
                                type="text" 
                                placeholder="Buscar evento..." 
                                className="bg-black border border-gray-600 p-2 text-white w-full md:w-64 focus:border-red-500 outline-none font-mono"
                                onChange={(e) => setSearchTerm(e.target.value)}
                                value={searchTerm}
                            />
                        </div>
                        <div>
                            <SectionTitle>La Ruleta Diaria ({filteredEvents.length})</SectionTitle>
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-4">
                                {filteredEvents.map((ev, idx) => (
                                    <EventCard key={idx} event={ev} />
                                ))}
                            </div>
                        </div>
                    </div>
                );
            case 'roles':
                return (
                    <div>
                        <SectionTitle>Ruleta Semanal (Roles)</SectionTitle>
                        <p className="text-gray-400 mb-6 text-lg">Estos efectos son personales y definen tu rol durante una semana completa.</p>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-4">
                            {weeklyRoles.map((role, idx) => (
                                <RoleCard key={idx} role={role} />
                            ))}
                        </div>
                    </div>
                );
            case 'mobs':
                return (
                    <div>
                        <SectionTitle>Bestiario ({mobs.length})</SectionTitle>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {mobs.map((m, i) => (
                                <MobCard key={i} mob={m} />
                            ))}
                        </div>
                    </div>
                );
            case 'items':
                return (
                    <div>
                        <SectionTitle>Ítems & Artefactos ({items.length})</SectionTitle>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {items.map((it, i) => (
                                <ItemCard key={i} item={it} />
                            ))}
                        </div>
                    </div>
                );
            case 'tutorials':
                return (
                    <div>
                        <SectionTitle>Tutoriales y Guías</SectionTitle>
                        <div className="grid gap-6">
                            {tutorials.map((t, i) => (
                                <div key={i} className="bg-zinc-900/80 border border-zinc-700 p-6 hover:border-red-900/50 transition-colors">
                                    <h3 className="text-3xl text-white mb-6 flex items-center gap-3 border-b border-zinc-800 pb-4">
                                        <span className="text-4xl">{t.icon}</span> {t.title}
                                    </h3>
                                    <div className="space-y-6">
                                        {t.content.map((c, j) => (
                                            <div key={j}>
                                                <h4 className="text-red-500 font-bold uppercase tracking-wider text-sm mb-2">{c.subtitle}</h4>
                                                <p className="text-gray-300 text-lg leading-relaxed">{c.text}</p>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                );
            default:
                return null;
        }
    };

    const WikiSidebarBtn = ({ id, label, icon }: { id: string, label: string, icon: string }) => (
        <button 
            onClick={() => setWikiSection(id)}
            className={`w-full text-left px-6 py-4 text-lg font-bold uppercase tracking-wider transition-all border-l-4 ${
                wikiSection === id 
                ? 'bg-red-900/20 border-red-600 text-white' 
                : 'border-transparent text-gray-500 hover:text-gray-300 hover:bg-zinc-900'
            }`}
        >
            <span className="mr-3">{icon}</span> {label}
        </button>
    );

    const renderContent = () => {
        switch(view) {
            case 'rules':
                return (
                    <div className="animate-in fade-in duration-500 max-w-4xl mx-auto">
                        <SectionTitle>Reglas del Servidor</SectionTitle>
                        <div className="space-y-4">
                            {serverRules.map((rule, idx) => (
                                <div key={idx} className="mc-card bg-zinc-900 p-6 flex gap-4">
                                    <div className="text-red-500 text-2xl font-bold select-none">{idx + 1}.</div>
                                    <div>
                                        <h3 className="text-2xl text-white mb-2 uppercase">{rule.title}</h3>
                                        <p className="text-gray-400 text-xl">{rule.desc}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                );

            case 'wiki':
                return (
                    <div className="flex flex-col lg:flex-row gap-8 animate-in fade-in duration-500 min-h-[60vh]">
                        {/* Sidebar */}
                        <aside className="lg:w-72 flex-shrink-0">
                            <div className="sticky top-28 bg-black/40 border border-zinc-800 backdrop-blur-sm">
                                <div className="p-4 border-b border-zinc-800">
                                    <h2 className="text-xl text-white font-bold uppercase tracking-widest">Wiki</h2>
                                </div>
                                <div className="flex flex-col">
                                    <WikiSidebarBtn id="events" label="Eventos" icon="🎲" />
                                    <WikiSidebarBtn id="roles" label="Roles" icon="⚔️" />
                                    <WikiSidebarBtn id="mobs" label="Bestiario" icon="🧟" />
                                    <WikiSidebarBtn id="items" label="Items" icon="🎒" />
                                    <WikiSidebarBtn id="tutorials" label="Tutoriales" icon="📚" />
                                </div>
                            </div>
                        </aside>

                        {/* Content Area */}
                        <div className="flex-1">
                            {renderWikiContent()}
                        </div>
                    </div>
                );
            
            case 'roles':
                // Redirect to wiki roles for consistency, or keep as standalone? 
                // Keeping standalone for now as per original design, but maybe user wants it unified.
                // Let's just render the same content but wrapped.
                return (
                    <div className="animate-in slide-in-from-bottom-4 duration-500">
                        <SectionTitle>Detalle de Roles Semanales</SectionTitle>
                        <p className="text-xl text-gray-400 mb-8">Información extendida sobre los modificadores personales.</p>
                        
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            {weeklyRoles.map((role, idx) => (
                                <RoleCard key={idx} role={role} />
                            ))}
                        </div>
                    </div>
                );

            case 'simulator':
                return (
                    <div className="flex flex-col items-center">
                        <div className="flex gap-4 mb-8">
                            <button 
                                onClick={() => setSimSection('roulette')}
                                className={`px-6 py-3 text-xl font-bold uppercase tracking-wider border-b-4 transition-colors ${
                                    simSection === 'roulette' 
                                    ? 'border-red-600 text-white' 
                                    : 'border-transparent text-gray-500 hover:text-gray-300'
                                }`}
                            >
                                Ruleta de Eventos
                            </button>
                            <button 
                                onClick={() => setSimSection('item')}
                                className={`px-6 py-3 text-xl font-bold uppercase tracking-wider border-b-4 transition-colors ${
                                    simSection === 'item' 
                                    ? 'border-blue-600 text-white' 
                                    : 'border-transparent text-gray-500 hover:text-gray-300'
                                }`}
                            >
                                Roll Diario de Items
                            </button>
                        </div>
                        
                        {simSection === 'roulette' ? <RouletteSimulator /> : <ItemRollSimulator />}
                    </div>
                );

            case 'active':
                return <ActiveModifiers />;

            default: // Home
                return (
                    <div className="text-center space-y-12 animate-in fade-in duration-700">
                        <div className="relative py-16">
                            <div className="absolute inset-0 bg-red-900/10 blur-3xl rounded-full"></div>
                            <h1 className="text-6xl md:text-9xl font-bold text-white mb-4 text-shadow relative z-10 tracking-tighter">
                                <span className="text-red-600">ROLL</span><span className="text-gray-700">AND</span>DEATH
                            </h1>
                            <p className="text-2xl md:text-3xl text-gray-400 tracking-[0.2em] uppercase font-bold">Protocolo Diciembre</p>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-6xl mx-auto">
                            <div className="bg-zinc-900/80 p-8 border-t-4 border-red-600 hover:bg-zinc-800 transition-colors cursor-default">
                                <div className="text-5xl mb-4">💀</div>
                                <h3 className="text-3xl text-white mb-2 uppercase">3 Vidas</h3>
                                <p className="text-xl text-gray-400">Sistema Hardcore con revivir limitado. Si pierdes todo, entras al Limbo hasta que alguien pague el precio.</p>
                            </div>
                            <div className="bg-zinc-900/80 p-8 border-t-4 border-purple-600 hover:bg-zinc-800 transition-colors cursor-default">
                                <div className="text-5xl mb-4">🎲</div>
                                <h3 className="text-3xl text-white mb-2 uppercase">Caos Acumulativo</h3>
                                <p className="text-xl text-gray-400">Cada día se suma una nueva regla. Día 1 = 1 Regla. Día 30 = 30 Reglas activas a la vez.</p>
                            </div>
                            <div className="bg-zinc-900/80 p-8 border-t-4 border-blue-600 hover:bg-zinc-800 transition-colors cursor-default">
                                <div className="text-5xl mb-4">⚔️</div>
                                <h3 className="text-3xl text-white mb-2 uppercase">Roles & RPG</h3>
                                <p className="text-xl text-gray-400">Roll Semanal personal. Obtén clases como "Vampiro", "Berserker" o "Tanque" con ventajas y debilidades únicas.</p>
                            </div>
                        </div>

                        <div className="pt-8 flex justify-center gap-4 flex-wrap">
                            <button onClick={() => setView('wiki')} className="bg-white text-black px-8 py-4 text-2xl font-bold hover:bg-gray-200 transition-colors uppercase tracking-wider">
                                Ver Eventos ({dailyEvents.length})
                            </button>
                            <button onClick={() => setView('rules')} className="border-2 border-white text-white px-8 py-4 text-2xl font-bold hover:bg-white/10 transition-colors uppercase tracking-wider">
                                Leer Reglas
                            </button>
                        </div>
                    </div>
                );
        }
    };

    return (
        <div className="min-h-screen pb-20">
            {/* Header */}
            <header className="border-b-4 border-zinc-800 bg-black/80 sticky top-0 z-50 backdrop-blur-md">
                <div className="container mx-auto px-4">
                    <div className="flex flex-col md:flex-row justify-between items-center py-4 gap-4">
                        <div 
                            className="text-4xl text-white font-bold tracking-tighter cursor-pointer hover:text-red-500 transition-colors select-none"
                            onClick={() => setView('home')}
                        >
                            R&D<span className="text-red-600">SMP</span>
                        </div>
                        <nav className="flex gap-2 overflow-x-auto w-full md:w-auto pb-2 md:pb-0 justify-center">
                            <NavButton active={view === 'home'} onClick={() => setView('home')}>Inicio</NavButton>
                            <NavButton active={view === 'active'} onClick={() => setView('active')}>Activos</NavButton>
                            <NavButton active={view === 'wiki'} onClick={() => setView('wiki')}>Wiki</NavButton>
                            <NavButton active={view === 'roles'} onClick={() => setView('roles')}>Roles</NavButton>
                            <NavButton active={view === 'rules'} onClick={() => setView('rules')}>Reglas</NavButton>
                            <NavButton active={view === 'simulator'} onClick={() => setView('simulator')}>Sim</NavButton>
                        </nav>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <main className="container mx-auto px-4 py-8">
                {renderContent()}
            </main>

            {/* Footer / Server Status */}
            <footer className="fixed bottom-0 w-full bg-zinc-900 border-t-2 border-zinc-700 p-2 text-center text-gray-500 text-lg z-40 flex justify-between px-4 md:justify-center md:gap-8">
                <div className="flex items-center gap-2">
                    <span className="w-3 h-3 bg-green-500 rounded-full animate-pulse shadow-[0_0_10px_#22c55e]"></span>
                    <span className="text-green-500 font-bold">ONLINE</span>
                </div>
                <div className="hidden md:block">|</div>
                <div className="font-mono text-gray-400 hover:text-white cursor-pointer select-all">play.rollanddeath.net</div>
            </footer>
        </div>
    );
}

export default App;
